package gov.cabinetoffice.gap.spotlight.publisher.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SendToSpotlightDto {

    @JsonProperty("Schemes")
    private List<SpotlightSchemeDto> schemes;

}
