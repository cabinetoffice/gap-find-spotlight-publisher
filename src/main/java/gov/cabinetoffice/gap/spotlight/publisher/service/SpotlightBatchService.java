package gov.cabinetoffice.gap.spotlight.publisher.service;

import gov.cabinetoffice.gap.spotlight.publisher.dto.batch.SpotlightBatchDto;
import gov.cabinetoffice.gap.spotlight.publisher.enums.SpotlightBatchStatus;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;

public class SpotlightBatchService {
    static final String SPOTLIGHT_BATCH_ENDPOINT = "/spotlight-batch";
    public static final String SPOTLIGHT_BATCH_MAX_SIZE = System.getenv("SPOTLIGHT_BATCH_MAX_SIZE");
    private static final Logger logger = LoggerFactory.getLogger(SpotlightBatchService.class);

    private static final OkHttpClient restClient = new OkHttpClient();

    public static Boolean existsByStatus(OkHttpClient restClient, SpotlightBatchStatus status) throws Exception {
        final Map<String, String> params = Map.of("batchSizeLimit", SPOTLIGHT_BATCH_MAX_SIZE);

        final String getEndpoint = SPOTLIGHT_BATCH_ENDPOINT + "/status/" + status + "/exists";

        logger.info("Sending get request to {}", getEndpoint);

        return RestService.sendGetRequest(restClient, params, getEndpoint, Boolean.class);
    }

    public static SpotlightBatchDto getSpotlightBatchByStatus(OkHttpClient restClient, SpotlightBatchStatus status) throws Exception {
        final Map<String, String> params = Map.of("batchSizeLimit", SPOTLIGHT_BATCH_MAX_SIZE);

        final String getEndpoint = SPOTLIGHT_BATCH_ENDPOINT + "/status/" + status;

        logger.info("Sending get request to {}", getEndpoint);

        return RestService.sendGetRequest(restClient, params, getEndpoint, SpotlightBatchDto.class);
    }

    public static SpotlightBatchDto createSpotlightBatch(OkHttpClient restClient) throws Exception {

        final String postEndpoint = SPOTLIGHT_BATCH_ENDPOINT;

        logger.info("Sending post request to {}", postEndpoint);

        return RestService.sendPostRequest(restClient, null, postEndpoint, SpotlightBatchDto.class);
    }

    public static SpotlightBatchDto createSpotlightBatchSubmissionRow(OkHttpClient restClient, UUID spotlightBatchId, UUID spotlightSubmissionId) throws Exception {

        final String patchEndpoint = SPOTLIGHT_BATCH_ENDPOINT + "/" + spotlightBatchId + "/add-spotlight-submission/" + spotlightSubmissionId;

        logger.info("Sending patch request to {}", patchEndpoint);

        return RestService.sendPatchRequest(restClient, null, patchEndpoint, SpotlightBatchDto.class);
    }

    public static void sendBatchesToSpotlight(OkHttpClient restClient) throws Exception {

        final String postEndpoint = SPOTLIGHT_BATCH_ENDPOINT + "/send-to-spotlight";

        logger.info("Sending post request to {}", postEndpoint);

        RestService.sendPostRequest(restClient, null, postEndpoint, String.class);
    }

    public static SpotlightBatchDto getAvailableSpotlightBatch() throws Exception {
        final Boolean existingBatch = SpotlightBatchService.existsByStatus(restClient, SpotlightBatchStatus.QUEUED);

        if (existingBatch.equals(Boolean.FALSE)) {
            return SpotlightBatchService.createSpotlightBatch(restClient);
        }

        return SpotlightBatchService.getSpotlightBatchByStatus(restClient, SpotlightBatchStatus.QUEUED);
    }
}
