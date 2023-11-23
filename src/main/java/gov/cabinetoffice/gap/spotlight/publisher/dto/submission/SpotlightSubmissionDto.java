package gov.cabinetoffice.gap.spotlight.publisher.dto.submission;


import gov.cabinetoffice.gap.spotlight.publisher.model.SchemeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SpotlightSubmissionDto {

    private UUID id;

    private SpotlightMandatoryQuestionDto mandatoryQuestions;

    private SchemeEntity grantScheme;

    private String status;

    private Instant lastSendAttempt;

    private int version;

    private Instant created;

    private Instant lastUpdated;

}
