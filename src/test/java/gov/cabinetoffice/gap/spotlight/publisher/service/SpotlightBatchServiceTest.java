package gov.cabinetoffice.gap.spotlight.publisher.service;

import gov.cabinetoffice.gap.spotlight.publisher.dto.batch.SpotlightBatchDto;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.UUID;

import static gov.cabinetoffice.gap.spotlight.publisher.enums.SpotlightBatchStatus.QUEUED;
import static gov.cabinetoffice.gap.spotlight.publisher.service.SpotlightBatchService.SPOTLIGHT_BATCH_ENDPOINT;
import static gov.cabinetoffice.gap.spotlight.publisher.service.SpotlightBatchService.SPOTLIGHT_BATCH_MAX_SIZE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class SpotlightBatchServiceTest {

    final Map<String, String> params = Map.of("batchSizeLimit", SPOTLIGHT_BATCH_MAX_SIZE);
    private final UUID spotlightBatchId = UUID.fromString("00000000-0000-0000-0000-000000000000");
    private final UUID spotlightSubmissionId = UUID.fromString("11111111-1111-1111-1111-111111111111");
    @Mock
    private OkHttpClient mockRestClient;

    @Nested
    class spotlightBatchWithStatusExist {

        @Test
        void spotlightBatchWithStatusExist_ReturnsTrue() throws Exception {
            try (MockedStatic<RestService> mockedRestService = mockStatic(RestService.class)) {
                final Boolean expectedResult = true;
                final String getEndpoint = SPOTLIGHT_BATCH_ENDPOINT + "/status/" + QUEUED + "/exists";

                mockedRestService.when(() -> RestService.sendGetRequest(
                                mockRestClient,
                                params,
                                getEndpoint,
                                Boolean.class))
                        .thenReturn(expectedResult);

                final Boolean result = SpotlightBatchService.existsByStatus(mockRestClient, QUEUED);

                mockedRestService.verify(() -> RestService.sendGetRequest(
                        mockRestClient,
                        params,
                        getEndpoint,
                        Boolean.class));

                assertThat(result).isEqualTo(expectedResult);
            }
        }

        @Test
        void spotlightBatchWithStatusExist_ReturnsFalse() throws Exception {
            try (MockedStatic<RestService> mockedRestService = mockStatic(RestService.class)) {
                final Boolean expectedResult = false;
                final String getEndpoint = SPOTLIGHT_BATCH_ENDPOINT + "/status/" + QUEUED + "/exists";

                mockedRestService.when(() -> RestService.sendGetRequest(
                                mockRestClient,
                                params,
                                getEndpoint,
                                Boolean.class))
                        .thenReturn(expectedResult);

                final Boolean result = SpotlightBatchService.existsByStatus(mockRestClient, QUEUED);

                mockedRestService.verify(() -> RestService.sendGetRequest(
                        mockRestClient,
                        params,
                        getEndpoint,
                        Boolean.class));

                assertThat(result).isEqualTo(expectedResult);
            }
        }
    }

    @Nested
    class getSpotlightBatchByStatus {
        @Test
        void getSpotlightBatchByStatus() throws Exception {
            try (MockedStatic<RestService> mockedRestService = mockStatic(RestService.class)) {
                final SpotlightBatchDto expectedResult = SpotlightBatchDto.builder()
                        .id(spotlightBatchId)
                        .build();
                final String getEndpoint = SPOTLIGHT_BATCH_ENDPOINT + "/status/" + QUEUED;

                mockedRestService.when(() -> RestService.sendGetRequest(
                                mockRestClient,
                                params,
                                getEndpoint,
                                SpotlightBatchDto.class))
                        .thenReturn(expectedResult);

                final SpotlightBatchDto result = SpotlightBatchService.
                        getSpotlightBatchByStatus(mockRestClient, QUEUED);

                mockedRestService.verify(() -> RestService.sendGetRequest(
                        mockRestClient,
                        params,
                        getEndpoint,
                        SpotlightBatchDto.class));

                assertThat(result).isEqualTo(expectedResult);
            }
        }
    }

    @Nested
    class createSpotlightBatch {
        @Test
        void testCreateSpotlightBatchSubmissionRow() throws Exception {
            try (MockedStatic<RestService> mockedRestService = mockStatic(RestService.class)) {
                final SpotlightBatchDto spotlightBatch = SpotlightBatchDto.builder()
                        .id(spotlightBatchId)
                        .build();

                final String postEndpoint = SPOTLIGHT_BATCH_ENDPOINT;

                mockedRestService.when(() -> RestService.sendPostRequest(
                                mockRestClient,
                                null,
                                postEndpoint,
                                SpotlightBatchDto.class))
                        .thenReturn(spotlightBatch);

                final SpotlightBatchDto result = SpotlightBatchService.createSpotlightBatch(mockRestClient);

                mockedRestService.verify(() -> RestService.sendPostRequest(
                        mockRestClient,
                        null,
                        postEndpoint,
                        SpotlightBatchDto.class));
                assertThat(result).isEqualTo(spotlightBatch);

            }
        }
    }

    @Nested
    class createSpotlightBatchSubmissionRow {
        @Test
        void createSpotlightBatchSubmissionRow() throws Exception {
            try (MockedStatic<RestService> mockedRestService = mockStatic(RestService.class)) {

                final String patchEndpoint = SPOTLIGHT_BATCH_ENDPOINT + "/" + spotlightBatchId + "/add-spotlight-submission/" + spotlightSubmissionId;

                SpotlightBatchService.createSpotlightBatchSubmissionRow(mockRestClient, spotlightBatchId, spotlightSubmissionId);

                mockedRestService.verify(() ->
                        RestService.sendPatchRequest(mockRestClient, null, patchEndpoint, SpotlightBatchDto.class)
                );

            }
        }
    }

    @Nested
    class sendBatchesToSpotlight {

        @Test
        void sendsBatchesSuccessfully() throws Exception {
            try (MockedStatic<RestService> mockedRestService = mockStatic(RestService.class)) {

                final String patchEndpoint = SPOTLIGHT_BATCH_ENDPOINT + "/send-to-spotlight";

                SpotlightBatchService.sendBatchesToSpotlight(mockRestClient);

                mockedRestService.verify(() ->
                        RestService.sendPostRequest(mockRestClient, null, patchEndpoint, String.class)
                );
            }
        }
    }


    @Nested
    class getAvailableSpotlightBatch {
        @Test
        void getAvailableSpotlightBatch_ExistingQueuedBatch() throws Exception {
            try (MockedStatic<SpotlightBatchService> mockedService = mockStatic(SpotlightBatchService.class)) {
                final SpotlightBatchDto existingBatch = SpotlightBatchDto.builder()
                        .id(UUID.fromString("00000000-0000-0000-0000-000000000001"))
                        .status(QUEUED)
                        .build();

                mockedService.when(() -> SpotlightBatchService.getSpotlightBatchByStatus(any(), eq(QUEUED)))
                        .thenReturn(existingBatch);

                mockedService.when(() -> SpotlightBatchService.getAvailableSpotlightBatch())
                        .thenCallRealMethod();

                mockedService.when(() -> SpotlightBatchService.existsByStatus(any(), eq(QUEUED)))
                        .thenReturn(Boolean.TRUE);

                final SpotlightBatchDto result = SpotlightBatchService.getAvailableSpotlightBatch();

                assertThat(result).isEqualTo(existingBatch);
                mockedService.verify(() -> SpotlightBatchService.createSpotlightBatch(any()), never());
            }
        }

        @Test
        void getAvailableSpotlightBatch_NoExistingBatch() throws Exception {
            try (MockedStatic<SpotlightBatchService> mockedService = mockStatic(SpotlightBatchService.class)) {
                final SpotlightBatchDto newBatch = SpotlightBatchDto.builder()
                        .id(UUID.fromString("00000000-0000-0000-0000-000000000001"))
                        .status(QUEUED)
                        .build();

                mockedService.when(() -> SpotlightBatchService.getSpotlightBatchByStatus(any(), eq(QUEUED)))
                        .thenReturn(null);

                mockedService.when(() -> SpotlightBatchService.createSpotlightBatch(any()))
                        .thenReturn(newBatch);

                mockedService.when(() -> SpotlightBatchService.getAvailableSpotlightBatch())
                        .thenCallRealMethod();

                mockedService.when(() -> SpotlightBatchService.existsByStatus(any(), eq(QUEUED)))
                        .thenReturn(Boolean.FALSE);

                final SpotlightBatchDto result = SpotlightBatchService.getAvailableSpotlightBatch();

                assertThat(result).isEqualTo(newBatch);
                mockedService.verify(() -> SpotlightBatchService.createSpotlightBatch(any()));
            }
        }
    }
}


