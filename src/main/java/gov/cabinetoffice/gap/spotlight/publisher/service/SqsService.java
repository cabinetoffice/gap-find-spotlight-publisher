package gov.cabinetoffice.gap.spotlight.publisher.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;
import software.amazon.awssdk.services.sqs.model.SqsException;

import java.util.ArrayList;
import java.util.List;

public class SqsService {

    private SqsService() {
        throw new IllegalStateException("Utility class");
    }

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

            //will get all the messages from the queue till there response from sqs contains messages
            do {
                receiveMessageResponse = sqsClient.receiveMessage(receiveMessageRequest);
                List<Message> messages = receiveMessageResponse.messages();
                allMessages.addAll(messages);

            } while ((receiveMessageResponse.messages().size() == 10));

            return allMessages;
        } catch (SqsException e) {
            logger.info("Error receiving messages from queue");
            logger.error(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return List.of();
    }

    public static void deleteMessageFromQueue(SqsClient sqsClient, Message message) {
        try {
            DeleteMessageRequest deleteMessageRequest = DeleteMessageRequest.builder()
                    .queueUrl(SPOTLIGHT_BATCH_QUEUE_URL)
                    .receiptHandle(message.receiptHandle())
                    .build();

            sqsClient.deleteMessage(deleteMessageRequest);
            logger.info("Message deleted from queue with receipt handle {} for spotlight submission id {}", message.receiptHandle(), message.body());

        } catch (SqsException e) {
            logger.info("Error deleting messages from queue with receipt handle {} for spotlight submission id {}", message.receiptHandle(), message.body());
            logger.error(e.awsErrorDetails().errorMessage());
        }
    }
}
