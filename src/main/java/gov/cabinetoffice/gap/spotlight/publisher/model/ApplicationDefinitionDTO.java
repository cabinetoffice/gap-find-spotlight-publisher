package gov.cabinetoffice.gap.spotlight.publisher.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationDefinitionDTO {
    private List<ApplicationFormSectionDTO> sections;
}
