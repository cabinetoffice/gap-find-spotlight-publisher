package gov.cabinetoffice.gap.spotlight.publisher.model;

import gov.cabinetoffice.gap.spotlight.publisher.enums.GrantMandatoryQuestionFundingLocation;
import gov.cabinetoffice.gap.spotlight.publisher.enums.GrantMandatoryQuestionOrgType;
import gov.cabinetoffice.gap.spotlight.publisher.enums.GrantMandatoryQuestionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GrantMandatoryQuestions {

    private UUID id;

    private SchemeEntity schemeEntity;

    private Submission submission;

    private String name;

    private String addressLine1;

    private String addressLine2;

    private String city;

    private String county;

    private String postcode;

    private GrantMandatoryQuestionOrgType orgType;

    private String companiesHouseNumber;

    private String charityCommissionNumber;

    private BigDecimal fundingAmount;

    private GrantMandatoryQuestionFundingLocation[] fundingLocation;

    @Builder.Default
    private GrantMandatoryQuestionStatus status = GrantMandatoryQuestionStatus.NOT_STARTED;

    @Builder.Default
    private Integer version = 1;

    @Builder.Default
    private Instant created = Instant.now();

    private GrantApplicant createdBy;

    private Instant lastUpdated;

    private GrantApplicant lastUpdatedBy;

    private String gapId;
}
