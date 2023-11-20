package gov.cabinetoffice.gap.spotlight.publisher.lambda;

import gov.cabinetoffice.gap.spotlight.publisher.dto.spotlightBatch.SpotlightBatchDto;
import gov.cabinetoffice.gap.spotlight.publisher.dto.spotlightSubmissions.SpotlightSubmissionDto;
import gov.cabinetoffice.gap.spotlight.publisher.model.SpotlightBatch;
import gov.cabinetoffice.gap.spotlight.publisher.model.SpotlightSubmission;
import gov.cabinetoffice.gap.spotlight.publisher.service.SpotlightBatchService;
import gov.cabinetoffice.gap.spotlight.publisher.service.SpotlightSubmissionService;
import gov.cabinetoffice.gap.spotlight.publisher.service.SqsService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

class HandlerTest {
    private static MockedStatic sqsClient;
    private static MockedStatic<SpotlightSubmissionService> mockedSpotlightSubmissionService;
    private static MockedStatic<SpotlightBatchService> mockedSpotlightBatchService;
    private static MockedStatic<SqsService> mockedSqsService;

    private final Map<String, Object> eventBridgeEvent = Map.of("String", "String");


    @BeforeAll
    static void beforeAll() {
        sqsClient = mockStatic(SqsClient.class);

        mockedSpotlightSubmissionService = mockStatic(SpotlightSubmissionService.class);
        mockedSpotlightBatchService = mockStatic(SpotlightBatchService.class);
        mockedSqsService = mockStatic(SqsService.class);

    }

    @AfterAll
    static void afterAll() {
        sqsClient.close();
        mockedSpotlightSubmissionService.close();
        mockedSpotlightBatchService.close();
        mockedSqsService.close();
    }

    @Test
    void SuccessfullyRunningThroughAllActions() {
        final UUID spotlightSubmissionId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        final UUID spotlightBatchId = UUID.fromString("22222222-2222-2222-2222-222222222222");
        final List<Message> messages = List.of(Message.builder().body(spotlightSubmissionId.toString()).build());
        final SpotlightSubmissionDto spotlightSubmission = SpotlightSubmissionDto.builder().id(spotlightSubmissionId).build();
        final SpotlightBatchDto spotlightBatch = SpotlightBatchDto.builder().id(spotlightBatchId).build();

        sqsClient.when(SqsClient::create).thenReturn(mock(SqsClient.class));
        mockedSqsService.when(() -> SqsService.grabMessagesFromQueue(any())).thenReturn(messages);
        mockedSpotlightSubmissionService.when(() -> SpotlightSubmissionService.getSpotlightSubmissionData(any(), any())).thenReturn(spotlightSubmission);
        mockedSpotlightBatchService.when(SpotlightBatchService::getAvailableSpotlightBatch).thenReturn(spotlightBatch);

        final Handler handler = new Handler();
        handler.handleRequest(eventBridgeEvent, null);

        mockedSqsService.verify(() -> SqsService.grabMessagesFromQueue(any()));
        mockedSpotlightSubmissionService.verify(() -> SpotlightSubmissionService.getSpotlightSubmissionData(any(), any()));
        mockedSpotlightBatchService.verify(SpotlightBatchService::getAvailableSpotlightBatch);
        mockedSqsService.verify(() -> SqsService.deleteMessageFromQueue(any(), any()));
    }

    @Test
    void ThrowException() {
        sqsClient.when(SqsClient::create).thenReturn(mock(SqsClient.class));
        mockedSqsService.when(() -> SqsService.grabMessagesFromQueue(any())).thenThrow(new RuntimeException());

        final Handler handler = new Handler();
        assertThrows(RuntimeException.class, () -> handler.handleRequest(eventBridgeEvent, null));

    }
}