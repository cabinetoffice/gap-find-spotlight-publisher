package gov.cabinetoffice.gap.spotlight.publisher.service;

import gov.cabinetoffice.gap.spotlight.publisher.dto.spotlightBatch.SpotlightBatchDto;
import gov.cabinetoffice.gap.spotlight.publisher.enums.SpotlightBatchStatus;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SpotlightBatchService {
    public static final String SPOTLIGHT_BATCH_MAX_SIZE = System.getenv("SPOTLIGHT_BATCH_MAX_SIZE");
    static final String SPOTLIGHT_BATCH_ENDPOINT = "/spotlight-batch";
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

    public static void createSpotlightBatchSubmissionRow(OkHttpClient restClient, UUID spotlightBatchId, UUID spotlightSubmissionId) throws Exception {

        final String patchEndpoint = SPOTLIGHT_BATCH_ENDPOINT + "/" + spotlightBatchId + "/add-spotlight-submission/" + spotlightSubmissionId;

        logger.info("Sending patch request to {}", patchEndpoint);

        RestService.sendPatchRequest(restClient, null, patchEndpoint);
    }

    public static List<SpotlightBatchDto> getBatchesToProcess(OkHttpClient restClient, SpotlightBatchStatus status) throws Exception {

        final String getEndpoint = SPOTLIGHT_BATCH_ENDPOINT + "/status/" + status + "/all";

        logger.info("Sending get request to {}", getEndpoint);

        return RestService.sendGetRequest(restClient, null, getEndpoint, List.class);


    }

    public static SpotlightBatchDto getAvailableSpotlightBatch() throws Exception {
        final Boolean batchExists = SpotlightBatchService.existsByStatus(restClient, SpotlightBatchStatus.QUEUED);

        if (batchExists.equals(Boolean.FALSE)) {
            return SpotlightBatchService.createSpotlightBatch(restClient);
        }

        return SpotlightBatchService.getSpotlightBatchByStatus(restClient, SpotlightBatchStatus.QUEUED);
    }


}
