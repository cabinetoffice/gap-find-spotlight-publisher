package gov.cabinetoffice.gap.spotlight.publisher.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SchemeEntity {
    private Integer id;

    private Integer funderId;

    @Builder.Default
    private Integer version = 1;

    @Builder.Default
    private Instant createdDate = Instant.now();

    private Integer createdBy;

    private Instant lastUpdated;

    private Integer lastUpdatedBy;

    private String ggisIdentifier;

    private String name;

    private String email;
}
