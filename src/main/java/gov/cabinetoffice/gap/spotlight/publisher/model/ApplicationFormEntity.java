package gov.cabinetoffice.gap.spotlight.publisher.model;

import gov.cabinetoffice.gap.spotlight.publisher.enums.ApplicationStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationFormEntity {

    private Integer grantApplicationId;

    private Integer grantSchemeId;

    private Integer version;

    private Instant created;

    private Integer createdBy;

    private Instant lastUpdated;

    private Integer lastUpdateBy;

    private Instant lastPublished;

    private String applicationName;

    private ApplicationStatusEnum applicationStatus;

    private ApplicationDefinitionDTO definition;
}
