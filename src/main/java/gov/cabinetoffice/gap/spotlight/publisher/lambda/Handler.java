package gov.cabinetoffice.gap.spotlight.publisher.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import gov.cabinetoffice.gap.spotlight.publisher.dto.batch.SpotlightBatchDto;
import gov.cabinetoffice.gap.spotlight.publisher.exceptions.SpotlightPublisherException;
import gov.cabinetoffice.gap.spotlight.publisher.service.SpotlightBatchService;
import gov.cabinetoffice.gap.spotlight.publisher.service.SqsService;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.Message;

import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Handler implements RequestHandler<Map<String, Object>, Void> {
    public static final boolean TESTING = Boolean.parseBoolean(System.getenv("TESTING"));
    private static final Logger logger = LoggerFactory.getLogger(Handler.class);
    
    // Configure OkHttpClient with longer timeouts for batch operations
    // Lambda timeout is 15 minutes, so set HTTP client to 10 minutes to allow buffer
    private static final OkHttpClient restClient = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.MINUTES)  // Long timeout for batch sending
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();
    
    private final SqsClient sqsClient = SqsClient.create();

    @Override
    public Void handleRequest(Map<String, Object> eventBridgeEvent, Context context) {

        // Always try to send existing QUEUED batches first (if any)
        try {
            sendBatchesToSpotlight();
            logger.info("Successfully sent existing batches to Spotlight");
        } catch (SocketTimeoutException e) {
            logger.warn("Timeout sending batches to Spotlight - backend may still be processing. Will retry next invocation.");
            // Don't fail - batches will be retried next time
        } catch (Exception e) {
            logger.error("Error sending existing batches to Spotlight", e);
            // Continue anyway - don't fail the whole invocation
        }

        final List<Message> messages = SqsService.grabMessagesFromQueue(sqsClient);

        if (messages.isEmpty()) {
            logger.info("No messages in the queue");
            return null;
        }

        try {
            // Limit messages to avoid timeout during processing
            final int MAX_MESSAGES_PER_INVOCATION = 500;
            final List<Message> messagesToProcess = messages.size() > MAX_MESSAGES_PER_INVOCATION
                    ? messages.subList(0, MAX_MESSAGES_PER_INVOCATION)
                    : messages;

            if (messages.size() > MAX_MESSAGES_PER_INVOCATION) {
                logger.info("Limiting processing to {} of {} messages to avoid timeout",
                        MAX_MESSAGES_PER_INVOCATION, messages.size());
            }

            // Step 1: create batches to process
            createBatches(messagesToProcess);

            // Step 2: send information to spotlight and process responses (after creating new batches)
            try {
                sendBatchesToSpotlight();
            } catch (SocketTimeoutException e) {
                logger.warn("Timeout sending batches to Spotlight after processing messages. Backend may still be processing.");
                // Don't throw - allow Lambda to complete successfully, batches will be retried next invocation
            } catch (Exception e) {
                logger.error("Error sending batches to Spotlight", e);
                // Don't throw - allow Lambda to complete successfully
            }

        } catch (Exception e) {
            logger.error("Could not process messages", e);
            throw new SpotlightPublisherException(e);
        }

        return null;
    }

    public void sendBatchesToSpotlight() throws Exception {
        SpotlightBatchService.sendBatchesToSpotlight(restClient);
    }

    public void createBatches(List<Message> messages) throws Exception {

        SpotlightBatchDto mostRecentBatch = SpotlightBatchService.getAvailableSpotlightBatch();
        logger.info("Spotlight batch with id {} has been retrieved", mostRecentBatch.getId());

        for (Message message : messages) {
            final SpotlightBatchDto currentBatch = getBatchToAddSubmissions(mostRecentBatch);
            logger.info("latest batch: {}", currentBatch.getId());

            final UUID spotlightSubmissionId = UUID.fromString(message.body());
            logger.info("Message in the queue has spotlight submission id {}", spotlightSubmissionId);

            mostRecentBatch = SpotlightBatchService.createSpotlightBatchSubmissionRow(restClient, currentBatch.getId(), spotlightSubmissionId);
            logger.info("spotlight submission with id {} has been added to spotlight batch with id {}", spotlightSubmissionId, currentBatch.getId());

            // delete from sqs when processed (commented out to make testing easier)
            if (!TESTING) {
                SqsService.deleteMessageFromQueue(sqsClient, message);
            }
        }
    }

    private int getRemainingSpacesInBatch(SpotlightBatchDto batch) {
        final int maxSize = Integer.parseInt(SpotlightBatchService.SPOTLIGHT_BATCH_MAX_SIZE);
        logger.info("batch max size: {}", maxSize);

        if (batch.getSpotlightSubmissions() == null) {
            logger.info("Batch is empty");
            return maxSize;
        }

        logger.info("remaining spaces in batch: {} ", (maxSize - batch.getSpotlightSubmissions().size()));
        return maxSize - batch.getSpotlightSubmissions().size();
    }

    private SpotlightBatchDto getBatchToAddSubmissions(SpotlightBatchDto mostRecentBatch) throws Exception {
        SpotlightBatchDto currentBatch;

        if (getRemainingSpacesInBatch(mostRecentBatch) > 0) {
            currentBatch = mostRecentBatch;
        } else {
            currentBatch = SpotlightBatchService.createSpotlightBatch(restClient);
        }

        return currentBatch;
    }
}