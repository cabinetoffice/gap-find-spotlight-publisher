package gov.cabinetoffice.gap.spotlight.publisher.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import gov.cabinetoffice.gap.spotlight.publisher.model.SpotlightBatch;
import gov.cabinetoffice.gap.spotlight.publisher.model.SpotlightSubmission;
import gov.cabinetoffice.gap.spotlight.publisher.service.SpotlightBatchService;
import gov.cabinetoffice.gap.spotlight.publisher.service.SpotlightSubmissionService;
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
    private static final Logger logger = LoggerFactory.getLogger(Handler.class);
    private static final OkHttpClient restClient = new OkHttpClient();

    private final SqsClient sqsClient = SqsClient.create();

    @Override
    public Void handleRequest(Map<String, Object> eventBridgeEvent, Context context) {

        //grab the messages from sqs
        final List<Message> messages = SqsService.grabMessagesFromQueue(sqsClient);

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


                final SpotlightBatch batch = SpotlightBatchService.getAvailableSpotlightBatch();
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