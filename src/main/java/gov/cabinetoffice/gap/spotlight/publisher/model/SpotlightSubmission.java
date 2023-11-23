package gov.cabinetoffice.gap.spotlight.publisher.model;

import gov.cabinetoffice.gap.spotlight.publisher.enums.SpotlightSubmissionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpotlightSubmission {
    private UUID id;

    private GrantMandatoryQuestions mandatoryQuestions;

    private SchemeEntity grantScheme;

    private SpotlightSubmissionStatus status;

    private Instant lastSendAttempt;

    private int version;

    private Instant created;

    private Instant lastUpdated;

    private List<SpotlightBatch> batches;
}
