package gov.cabinetoffice.gap.spotlight.publisher.service;

import gov.cabinetoffice.gap.spotlight.publisher.model.SpotlightSubmission;
import okhttp3.OkHttpClient;

import java.util.UUID;

public class SpotlightSubmissionService {

    public static SpotlightSubmission getSpotlightSubmissionData(OkHttpClient restClient, UUID spotlightSubmissionId) throws Exception {

        final String getEndpoint = "/spotlight-submissions/" + spotlightSubmissionId;

        return RestService.sendGetRequest(restClient, null, getEndpoint, SpotlightSubmission.class);
    }

}
