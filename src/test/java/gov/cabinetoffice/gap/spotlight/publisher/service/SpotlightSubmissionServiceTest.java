package gov.cabinetoffice.gap.spotlight.publisher.service;

import gov.cabinetoffice.gap.spotlight.publisher.dto.submission.SpotlightSubmissionDto;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;


@ExtendWith(MockitoExtension.class)
class SpotlightSubmissionServiceTest {
    private final UUID spotlightSubmissionId = UUID.fromString("00000000-0000-0000-0000-000000000000");
    @Mock
    private OkHttpClient mockRestClient;

    @Test
    void testGetSpotlightSubmissionData() throws Exception {
        try (MockedStatic<RestService> mockedRestService = mockStatic(RestService.class)) {
            SpotlightSubmissionDto expectedSpotlightSubmission = SpotlightSubmissionDto.builder().build();

            mockedRestService.when(() -> RestService.sendGetRequest(
                            mockRestClient,
                            null,
                            "/spotlight-submissions/" + spotlightSubmissionId,
                            SpotlightSubmissionDto.class))
                    .thenReturn(expectedSpotlightSubmission);

            final SpotlightSubmissionDto result = SpotlightSubmissionService.
                    getSpotlightSubmissionData(mockRestClient, spotlightSubmissionId);

            mockedRestService.verify(() -> RestService.sendGetRequest(
                    mockRestClient,
                    null,
                    "/spotlight-submissions/" + spotlightSubmissionId,
                    SpotlightSubmissionDto.class));

            assertThat(result).isEqualTo(expectedSpotlightSubmission);
        }
    }
}