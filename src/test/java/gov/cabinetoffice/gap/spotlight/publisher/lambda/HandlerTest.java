package gov.cabinetoffice.gap.spotlight.publisher.lambda;

import gov.cabinetoffice.gap.spotlight.publisher.dto.spotlightBatch.SpotlightBatchDto;
import gov.cabinetoffice.gap.spotlight.publisher.enums.SpotlightBatchStatus;
import gov.cabinetoffice.gap.spotlight.publisher.service.SpotlightBatchService;
import gov.cabinetoffice.gap.spotlight.publisher.service.SpotlightSubmissionService;
import gov.cabinetoffice.gap.spotlight.publisher.service.SqsService;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

class HandlerTest {
    private static MockedStatic sqsClient;
    private static MockedStatic<SpotlightSubmissionService> mockedSpotlightSubmissionService;
    private static MockedStatic<SpotlightBatchService> mockedSpotlightBatchService;
    private static MockedStatic<SqsService> mockedSqsService;

    private final Map<String, Object> eventBridgeEvent = Map.of("String", "String");

    private Handler handler;


    @BeforeEach
    void beforeEach() {
        sqsClient = mockStatic(SqsClient.class);

        mockedSpotlightSubmissionService = mockStatic(SpotlightSubmissionService.class);
        mockedSpotlightBatchService = mockStatic(SpotlightBatchService.class);
        mockedSqsService = mockStatic(SqsService.class);

        handler = Mockito.spy(new Handler());
    }

    @AfterEach
    void afterEach() {
        sqsClient.close();
        mockedSpotlightSubmissionService.close();
        mockedSpotlightBatchService.close();
        mockedSqsService.close();
    }

    @Test
    void handleRequest_Empty_Message_Queue() throws Exception {
        final List<Message> messages = Collections.emptyList();

        sqsClient.when(SqsClient::create).thenReturn(mock(SqsClient.class));
        mockedSqsService.when(() -> SqsService.grabMessagesFromQueue(any())).thenReturn(messages);

        handler.handleRequest(eventBridgeEvent, null);

        verify(handler, never()).createBatches(messages);
    }

    @Test
    void handleRequest_Success() throws Exception {
        final UUID spotlightSubmissionId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        final UUID spotlightBatchId = UUID.fromString("22222222-2222-2222-2222-222222222222");

        final List<Message> messages = List.of(Message.builder().body(spotlightSubmissionId.toString()).build());
        final SpotlightBatchDto spotlightBatch = SpotlightBatchDto.builder().id(spotlightBatchId).build();

        sqsClient.when(SqsClient::create).thenReturn(mock(SqsClient.class));
        mockedSqsService.when(() -> SqsService.grabMessagesFromQueue(any())).thenReturn(messages);
        mockedSpotlightBatchService.when(SpotlightBatchService::getAvailableSpotlightBatch).thenReturn(spotlightBatch);

        handler.handleRequest(eventBridgeEvent, null);

        mockedSqsService.verify(() -> SqsService.grabMessagesFromQueue(any()));
        mockedSpotlightBatchService.verify(SpotlightBatchService::getAvailableSpotlightBatch);
        verify(handler).createBatches(messages);
    }

    @Test
    void ThrowException() throws Exception {
        final UUID spotlightSubmissionId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        final List<Message> messages = List.of(Message.builder().body(spotlightSubmissionId.toString()).build());

        sqsClient.when(SqsClient::create).thenReturn(mock(SqsClient.class));
        mockedSqsService.when(() -> SqsService.grabMessagesFromQueue(any())).thenReturn(messages);

        doThrow(RuntimeException.class)
                .when(handler).createBatches(messages);

        assertThrows(RuntimeException.class, () -> handler.handleRequest(eventBridgeEvent, null));

    }

    @Test
    void createBatches() throws Exception {

        final SpotlightBatchDto existingBatch = SpotlightBatchDto.builder()
        .id(UUID.fromString("00000000-0000-0000-0000-000000000001"))
        .status(SpotlightBatchStatus.QUEUED)
        .build();

        final UUID spotlightSubmissionId = UUID.fromString("11111111-1111-1111-1111-111111111111");

        final List<Message> messages = List.of(Message.builder().body(spotlightSubmissionId.toString()).build());

        mockedSpotlightBatchService.when(() -> SpotlightBatchService.getAvailableSpotlightBatch())
                .thenReturn(existingBatch);

        handler.createBatches(messages);

        mockedSpotlightBatchService.verify(() -> SpotlightBatchService.createSpotlightBatchSubmissionRow(Mockito.any(), Mockito.eq(existingBatch.getId()), Mockito.eq(spotlightSubmissionId)));
        //mockedSqsService.verify(() -> SqsService.deleteMessage(Mockito.any(), Mockito.eq(messages.get(0))));
    }
}