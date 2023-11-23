package gov.cabinetoffice.gap.spotlight.publisher.model;

import gov.cabinetoffice.gap.spotlight.publisher.enums.GrantApplicantOrganisationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GrantApplicantOrganisationProfile {

    private long id;

    private GrantApplicant applicant;

    private String legalName;

    private GrantApplicantOrganisationType type;

    private String addressLine1;

    private String addressLine2;

    private String town;

    private String county;

    private String postcode;

    private String charityCommissionNumber;

    private String companiesHouseNumber;

}
