package gov.cabinetoffice.gap.spotlight.publisher.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GrantApplicant {

    private long id;

    private String userId;

    private GrantApplicantOrganisationProfile organisationProfile;

    @Builder.Default
    private List<Submission> submissions = new ArrayList<>();

}
