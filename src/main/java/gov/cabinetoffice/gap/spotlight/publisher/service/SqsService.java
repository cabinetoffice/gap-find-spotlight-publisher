package gov.cabinetoffice.gap.spotlight.publisher.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;
import software.amazon.awssdk.services.sqs.model.SqsException;

import java.util.ArrayList;
import java.util.List;

public class SqsService {
    private static final Logger logger = LoggerFactory.getLogger(SqsService.class);
    private static final String SPOTLIGHT_BATCH_QUEUE_URL = System.getenv("SPOTLIGHT_BATCH_QUEUE_URL");

    public static List<Message> grabMessagesFromQueue(SqsClient sqsClient) {

        List<Message> allMessages = new ArrayList<>();

        try {
            ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder()
                    .queueUrl(SPOTLIGHT_BATCH_QUEUE_URL)
                    .maxNumberOfMessages(10) // Set to the maximum allowed value
                    .build();

            ReceiveMessageResponse receiveMessageResponse;
            do {
                receiveMessageResponse = sqsClient.receiveMessage(receiveMessageRequest);
                List<Message> messages = receiveMessageResponse.messages();
                allMessages.addAll(messages);

                // If there are more messages, adjust the next request's visibilityTimeout
                if (!messages.isEmpty()) {
                    receiveMessageRequest = receiveMessageRequest.toBuilder()
                            .visibilityTimeout(0) // Set to 0 to make messages immediately visible again
                            .build();
                }
            } while ((receiveMessageResponse.messages().size() == 10));
            return allMessages;
        } catch (SqsException e) {
            logger.info("Error receiving messages from queue");
            logger.error(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return List.of();
    }
}