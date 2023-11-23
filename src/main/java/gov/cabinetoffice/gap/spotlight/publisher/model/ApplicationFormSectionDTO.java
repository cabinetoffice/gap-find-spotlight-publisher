package gov.cabinetoffice.gap.spotlight.publisher.model;

import gov.cabinetoffice.gap.spotlight.publisher.enums.SectionStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationFormSectionDTO {
    private String sectionId;

    private String sectionTitle;

    private SectionStatusEnum sectionStatus;

    private List<ApplicationFormQuestionDTO> questions;
}
