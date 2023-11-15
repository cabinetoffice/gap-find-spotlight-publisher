package gov.cabinetoffice.gap.spotlight.publisher.service;

import gov.cabinetoffice.gap.spotlight.publisher.enums.SpotlightBatchStatus;
import gov.cabinetoffice.gap.spotlight.publisher.model.SpotlightBatch;
import okhttp3.OkHttpClient;

import java.util.Map;
import java.util.UUID;

public class SpotlightBatchService {
    static final String SPOTLIGHT_BATCH_ENDPOINT = "/spotlight-batch";
    static final String SPOTLIGHT_BATCH_MAX_SIZE = System.getenv("SPOTLIGHT_BATCH_MAX_SIZE");

    public static Boolean spotlightBatchWithStatusExist(OkHttpClient restClient, SpotlightBatchStatus status) throws Exception {
        final Map<String, String> params = Map.of("batchSizeLimit", SPOTLIGHT_BATCH_MAX_SIZE);

        final String getEndpoint = SPOTLIGHT_BATCH_ENDPOINT + "/status/" + status + "/exists";

        return RestService.sendGetRequest(restClient, params, getEndpoint, Boolean.class);
    }

    public static SpotlightBatch getSpotlightBatchByStatus(OkHttpClient restClient, SpotlightBatchStatus status) throws Exception {
        final Map<String, String> params = Map.of("batchSizeLimit", SPOTLIGHT_BATCH_MAX_SIZE);

        final String getEndpoint = SPOTLIGHT_BATCH_ENDPOINT + "/status/" + status;

        return RestService.sendGetRequest(restClient, params, getEndpoint, SpotlightBatch.class);
    }

    public static SpotlightBatch createSpotlightBatch(OkHttpClient restClient) throws Exception {

        final String postEndpoint = SPOTLIGHT_BATCH_ENDPOINT;

        return RestService.sendPostRequest(restClient, null, postEndpoint);
    }

    public static void createSpotlightBatchSubmissionRow(OkHttpClient restClient, UUID spotlightBatchId, UUID spotlightSubmissionId) throws Exception {

        final String patchEndpoint = SPOTLIGHT_BATCH_ENDPOINT + "/" + spotlightBatchId + "/add-spotlight-submission/" + spotlightSubmissionId;

        RestService.sendPatchRequest(restClient, null, patchEndpoint);
    }
}
