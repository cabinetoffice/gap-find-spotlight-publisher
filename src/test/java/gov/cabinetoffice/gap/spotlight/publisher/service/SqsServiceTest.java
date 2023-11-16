package gov.cabinetoffice.gap.spotlight.publisher.service;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.slf4j.Logger;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SqsServiceTest {

    private static SqsClient sqsClient;
    private static Logger logger;
    private final ArgumentCaptor<ReceiveMessageRequest> receiveMessageRequestCaptor = ArgumentCaptor
            .forClass(ReceiveMessageRequest.class);
    private SqsService sqsService;

    @BeforeAll
    static void beforeAll() {
        sqsClient = mock(SqsClient.class);
        logger = mock(Logger.class);
    }

    @BeforeEach
    void resetMocks() {
        reset(sqsClient, logger);
        sqsService = new SqsService();
    }

    @Test
    void grabMessagesFromQueue_Success() {
        final List<Message> messages1 = createMessages(1, 10);
        final List<Message> messages2 = createMessages(11, 18);

        final ReceiveMessageResponse receiveMessageResponse1 = ReceiveMessageResponse.builder()
                .messages(messages1)
                .build();

        final ReceiveMessageResponse receiveMessageResponse2 = ReceiveMessageResponse.builder()
                .messages(messages2)
                .build();

        try (MockedStatic<SqsService> mockedService = mockStatic(SqsService.class)) {
            when(sqsClient.receiveMessage(any(ReceiveMessageRequest.class)))
                    .thenReturn(receiveMessageResponse1, receiveMessageResponse2);

            mockedService.when(() -> SqsService.grabMessagesFromQueue(any(SqsClient.class)))
                    .thenCallRealMethod();

            final List<Message> messages = SqsService.grabMessagesFromQueue(sqsClient);

            verify(sqsClient, times(2)).receiveMessage(receiveMessageRequestCaptor.capture());

            final List<ReceiveMessageRequest> capturedRequests = receiveMessageRequestCaptor.getAllValues();
            assertEquals(2, capturedRequests.size());
            assertEquals(18, messages.size());
        }
    }

    @Test
    void grabMessagesFromQueue_EmptyQueue() {
        final ReceiveMessageResponse emptyResponse = ReceiveMessageResponse.builder().messages(List.of()).build();
        try (MockedStatic<SqsService> mockedService = mockStatic(SqsService.class)) {
            when(sqsClient.receiveMessage(any(ReceiveMessageRequest.class))).thenReturn(emptyResponse);

            mockedService.when(() -> SqsService.grabMessagesFromQueue(any(SqsClient.class)))
                    .thenCallRealMethod();

            final List<Message> messages = SqsService.grabMessagesFromQueue(sqsClient);

            verify(sqsClient).receiveMessage(receiveMessageRequestCaptor.capture());
            assertEquals(0, messages.size());
        }
    }

    @Test
    void grabMessagesFromQueue_Exception() {
        when(sqsClient.receiveMessage(any(ReceiveMessageRequest.class))).thenThrow(AwsServiceException.class);

        assertThrows(AwsServiceException.class, () -> SqsService.grabMessagesFromQueue(sqsClient));
    }


    private List<Message> createMessages(int start, int end) {
        final List<Message> messages = Lists.newArrayList();
        for (int i = start; i <= end; i++) {
            messages.add(Message.builder().body("Message " + i).build());
        }
        return messages;
    }
}