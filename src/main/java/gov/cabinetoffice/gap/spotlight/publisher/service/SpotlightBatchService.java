package gov.cabinetoffice.gap.spotlight.publisher.service;

import gov.cabinetoffice.gap.spotlight.publisher.enums.SpotlightBatchStatus;
import gov.cabinetoffice.gap.spotlight.publisher.model.SpotlightBatch;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;

import static java.lang.Boolean.TRUE;

public class SpotlightBatchService {
    static final String SPOTLIGHT_BATCH_ENDPOINT = "/spotlight-batch";
    static final String SPOTLIGHT_BATCH_MAX_SIZE = System.getenv("SPOTLIGHT_BATCH_MAX_SIZE");
    private static final Logger logger = LoggerFactory.getLogger(SpotlightBatchService.class);

    private static final OkHttpClient restClient = new OkHttpClient();

    public static Boolean spotlightBatchWithStatusExist(OkHttpClient restClient, SpotlightBatchStatus status) throws Exception {
        final Map<String, String> params = Map.of("batchSizeLimit", SPOTLIGHT_BATCH_MAX_SIZE);

        final String getEndpoint = SPOTLIGHT_BATCH_ENDPOINT + "/status/" + status + "/exists";

        logger.info("Sending get request to {}", getEndpoint)   ;

        return RestService.sendGetRequest(restClient, params, getEndpoint, Boolean.class);
    }

    public static SpotlightBatch getSpotlightBatchByStatus(OkHttpClient restClient, SpotlightBatchStatus status) throws Exception {
        final Map<String, String> params = Map.of("batchSizeLimit", SPOTLIGHT_BATCH_MAX_SIZE);

        final String getEndpoint = SPOTLIGHT_BATCH_ENDPOINT + "/status/" + status;

        logger.info("Sending get request to {}", getEndpoint)   ;

        return RestService.sendGetRequest(restClient, params, getEndpoint, SpotlightBatch.class);
    }

    public static SpotlightBatch createSpotlightBatch(OkHttpClient restClient) throws Exception {

        final String postEndpoint = SPOTLIGHT_BATCH_ENDPOINT;

        logger.info("Sending post request to {}", postEndpoint)   ;


        return RestService.sendPostRequest(restClient, null, postEndpoint);
    }

    public static void createSpotlightBatchSubmissionRow(OkHttpClient restClient, UUID spotlightBatchId, UUID spotlightSubmissionId) throws Exception {

        final String patchEndpoint = SPOTLIGHT_BATCH_ENDPOINT + "/" + spotlightBatchId + "/add-spotlight-submission/" + spotlightSubmissionId;

        logger.info("Sending patch request to {}", patchEndpoint)   ;


        RestService.sendPatchRequest(restClient, null, patchEndpoint);
    }

    public static SpotlightBatch getAvailableSpotlightBatch() throws Exception {
        //check if there is a spotlight_batch in the db (take the latest?? with queued state)

        SpotlightBatch batch;

        //we check if a spotlight_batch with status QUEUED and spotlight_submissions (linked to it )size less than 200 exists
        final Boolean existingBatch = SpotlightBatchService.spotlightBatchWithStatusExist(restClient, SpotlightBatchStatus.QUEUED);

        logger.info("Spotlight batch with status QUEUED exists: {}", existingBatch);

        // if no create one
        if (existingBatch.equals(TRUE)) {
            batch = SpotlightBatchService.getSpotlightBatchByStatus(restClient, SpotlightBatchStatus.QUEUED);
        } else {
            batch = SpotlightBatchService.createSpotlightBatch(restClient);
        }

        return batch;
    }
}
