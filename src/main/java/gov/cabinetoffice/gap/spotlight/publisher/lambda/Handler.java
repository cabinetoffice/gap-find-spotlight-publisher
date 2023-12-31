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

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Handler implements RequestHandler<Map<String, Object>, Void> {
    public static final boolean TESTING = Boolean.parseBoolean(System.getenv("TESTING"));
    private static final Logger logger = LoggerFactory.getLogger(Handler.class);
    private static final OkHttpClient restClient = new OkHttpClient();
    private final SqsClient sqsClient = SqsClient.create();

    @Override
    public Void handleRequest(Map<String, Object> eventBridgeEvent, Context context) {

        final List<Message> messages = SqsService.grabMessagesFromQueue(sqsClient);

        if (messages.isEmpty()) {
            logger.info("No messages in the queue");
            return null;
        }

        try {

            /// step 1: create batches to process
            createBatches(messages);

            // step 2: send information to spotlight and process responses
            sendBatchesToSpotlight();

        } catch (Exception e) {
            logger.error("Could not process message ", e);
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