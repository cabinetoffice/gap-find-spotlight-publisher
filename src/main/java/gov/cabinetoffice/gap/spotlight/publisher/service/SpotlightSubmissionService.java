package gov.cabinetoffice.gap.spotlight.publisher.service;

import gov.cabinetoffice.gap.spotlight.publisher.model.SpotlightSubmission;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class SpotlightSubmissionService {
    private static final Logger logger = LoggerFactory.getLogger(SpotlightSubmissionService.class);

    public static SpotlightSubmission getSpotlightSubmissionData(OkHttpClient restClient, UUID spotlightSubmissionId) throws Exception {

        final String getEndpoint = "/spotlight-submissions/" + spotlightSubmissionId;

        logger.info("Sending get request to {}", getEndpoint)   ;

        return RestService.sendGetRequest(restClient, null, getEndpoint, SpotlightSubmission.class);
    }

}
