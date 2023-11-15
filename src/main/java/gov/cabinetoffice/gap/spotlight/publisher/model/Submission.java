package gov.cabinetoffice.gap.spotlight.publisher.model;

import gov.cabinetoffice.gap.spotlight.publisher.enums.SubmissionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Submission {

    private UUID id;

    @ToString.Exclude
    private GrantApplicant applicant;

    @ToString.Exclude
    private SchemeEntity scheme;

    @ToString.Exclude
    private ApplicationFormEntity application;

    private int version;

    private String  created;

    @ToString.Exclude
    private GrantApplicant createdBy;

    private String  lastUpdated;

    @ToString.Exclude
    private GrantApplicant lastUpdatedBy;

    private String submittedDate;

    private String applicationName;

    private SubmissionStatus status;

    private SubmissionDefinition definition;

    private String gapId;

    private Instant lastRequiredChecksExport;

}
