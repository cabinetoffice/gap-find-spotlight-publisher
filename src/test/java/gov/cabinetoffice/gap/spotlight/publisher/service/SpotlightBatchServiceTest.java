package gov.cabinetoffice.gap.spotlight.publisher.service;

import gov.cabinetoffice.gap.spotlight.publisher.enums.SpotlightBatchStatus;
import gov.cabinetoffice.gap.spotlight.publisher.model.SpotlightBatch;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.UUID;

import static gov.cabinetoffice.gap.spotlight.publisher.service.SpotlightBatchService.SPOTLIGHT_BATCH_ENDPOINT;
import static gov.cabinetoffice.gap.spotlight.publisher.service.SpotlightBatchService.SPOTLIGHT_BATCH_MAX_SIZE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;

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
                final String getEndpoint = SPOTLIGHT_BATCH_ENDPOINT + "/status/" + SpotlightBatchStatus.QUEUED + "/exists";

                mockedRestService.when(() -> RestService.sendGetRequest(
                                mockRestClient,
                                params,
                                getEndpoint,
                                Boolean.class))
                        .thenReturn(expectedResult);

                final Boolean result = SpotlightBatchService.spotlightBatchWithStatusExist(mockRestClient, SpotlightBatchStatus.QUEUED);

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
                final String getEndpoint = SPOTLIGHT_BATCH_ENDPOINT + "/status/" + SpotlightBatchStatus.QUEUED + "/exists";

                mockedRestService.when(() -> RestService.sendGetRequest(
                                mockRestClient,
                                params,
                                getEndpoint,
                                Boolean.class))
                        .thenReturn(expectedResult);

                final Boolean result = SpotlightBatchService.spotlightBatchWithStatusExist(mockRestClient, SpotlightBatchStatus.QUEUED);

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
                final SpotlightBatch expectedResult = SpotlightBatch.builder()
                        .id(spotlightBatchId)
                        .build();
                final String getEndpoint = SPOTLIGHT_BATCH_ENDPOINT + "/status/" + SpotlightBatchStatus.QUEUED;

                mockedRestService.when(() -> RestService.sendGetRequest(
                                mockRestClient,
                                params,
                                getEndpoint,
                                SpotlightBatch.class))
                        .thenReturn(expectedResult);

                final SpotlightBatch result = SpotlightBatchService.
                        getSpotlightBatchByStatus(mockRestClient, SpotlightBatchStatus.QUEUED);

                mockedRestService.verify(() -> RestService.sendGetRequest(
                        mockRestClient,
                        params,
                        getEndpoint,
                        SpotlightBatch.class));

                assertThat(result).isEqualTo(expectedResult);
            }
        }
    }

    @Nested
    class createSpotlightBatch {
        @Test
        void testCreateSpotlightBatchSubmissionRow() throws Exception {
            try (MockedStatic<RestService> mockedRestService = mockStatic(RestService.class)) {
                final SpotlightBatch spotlightBatch = SpotlightBatch.builder()
                        .id(spotlightBatchId)
                        .build();

                final String postEndpoint = SPOTLIGHT_BATCH_ENDPOINT;

                mockedRestService.when(() -> RestService.sendPostRequest(
                                mockRestClient,
                                null,
                                postEndpoint))
                        .thenReturn(spotlightBatch);

                final SpotlightBatch result = SpotlightBatchService.createSpotlightBatch(mockRestClient);

                mockedRestService.verify(() -> RestService.sendPostRequest(
                        mockRestClient,
                        null,
                        postEndpoint));
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
                        RestService.sendPatchRequest(mockRestClient, null, patchEndpoint)
                );

            }
        }
    }
}

