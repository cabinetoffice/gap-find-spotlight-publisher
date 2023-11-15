package gov.cabinetoffice.gap.spotlight.publisher.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpotlightBatch {

    private UUID id;

    private String status;

    private String lastSendAttempt;
    @Builder.Default
    private int version=1;

    private String created;

    private String lastUpdated;

    private List<SpotlightSubmission> spotlightSubmissions;
}
