package gov.cabinetoffice.gap.spotlight.publisher.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import gov.cabinetoffice.gap.spotlight.publisher.enums.SpotlightBatchStatus;
import gov.cabinetoffice.gap.spotlight.publisher.model.SpotlightBatch;
import gov.cabinetoffice.gap.spotlight.publisher.model.SpotlightSubmission;
import gov.cabinetoffice.gap.spotlight.publisher.service.SpotlightBatchService;
import gov.cabinetoffice.gap.spotlight.publisher.service.SpotlightSubmissionService;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.SqsException;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.lang.Boolean.TRUE;

public class Handler implements RequestHandler<Map<String, Object>, Void> {
    private static final Logger logger = LoggerFactory.getLogger(Handler.class);
    private static final OkHttpClient restClient = new OkHttpClient();

    private final SqsClient sqsClient = SqsClient.create();
    private final String spotlightBatchQueueUrl = System.getenv("SPOTLIGHT_BATCH_QUEUE_URL");

    public static List<Message> grabMessagesFromQueue(SqsClient sqsClient, String queueUrl) {

        try {
            final ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .maxNumberOfMessages(5)
                    .build();

            return sqsClient.receiveMessage(receiveMessageRequest).messages();
        } catch (SqsException e) {
            logger.info("Error receiving messages from queue");
            logger.error(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return List.of();
    }

    private static SpotlightBatch getAvailableSpotlightBatch() throws Exception {
        //check if there is a spotlight_batch in the db (take the latest?? with queued state)

        SpotlightBatch batch;

        //we check if a spotlight_batch with status QUEUED and spotlight_submissions (linked to it )size less than 200 exists
        final Boolean existingBatch = SpotlightBatchService.spotlightBatchWithStatusExist(restClient, SpotlightBatchStatus.QUEUED);

        // if no create one
        if (existingBatch.equals(TRUE)) {
            batch = SpotlightBatchService.getSpotlightBatchByStatus(restClient, SpotlightBatchStatus.QUEUED);
        } else {
            batch = SpotlightBatchService.createSpotlightBatch(restClient);
        }


        return batch;
    }

    @Override
    public Void handleRequest(Map<String, Object> sqsEvent, Context context) {

        //grab the messages from sqs
        final List<Message> messages = grabMessagesFromQueue(sqsClient, spotlightBatchQueueUrl);

        if (messages.isEmpty()) {
            logger.info("No messages in the queue");
            return null;
        }
        try {
            //loop through the messages of the queue
            for (Message message : messages) {

                //  from each message of the queue, get though the spotlight_submission id(contained in the message) the whole data from database
                final UUID spotlightSubmissionId = UUID.fromString(message.body());
                logger.info("Message in the queue has spotlight submission id {}", spotlightSubmissionId);
                //  from it get the mandatory_question and what else needed

                final SpotlightSubmission spotlightSubmission = SpotlightSubmissionService.getSpotlightSubmissionData(restClient, spotlightSubmissionId);
                logger.info("Spotlight submission with id {} has been retrieved", spotlightSubmissionId);

                final SpotlightBatch batch = getAvailableSpotlightBatch();
                logger.info("Spotlight batch with id {} has been retrieved", batch.getId());
                // add the spotlight_batch_id and the spotlight_submission_id to the spotlight_batch_submission

                SpotlightBatchService.createSpotlightBatchSubmissionRow(restClient, batch.getId(), spotlightSubmissionId);
                logger.info("spotlight submission with id {} has been added to spotlight batch with id {}", spotlightSubmissionId, batch.getId());
                //send batch to spotlight
                //TODO use spotlightSubmission data to send to spotlight
            }


        } catch (Exception e) {
            logger.error("Could not process message ", e);
            throw new RuntimeException(e);
        }
        return null;
    }


}