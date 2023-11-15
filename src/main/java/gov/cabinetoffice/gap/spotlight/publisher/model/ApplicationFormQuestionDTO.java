package gov.cabinetoffice.gap.spotlight.publisher.model;

import gov.cabinetoffice.gap.spotlight.publisher.enums.ResponseTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApplicationFormQuestionDTO {

    private String questionId;

    private String profileField;

    private String fieldPrefix;

    private String fieldTitle;

    private String hintText;

    private String adminSummary;

    private String displayText;

    private String questionSuffix;

    private ResponseTypeEnum responseType;

    private Map<String, Object> validation;

    private List<String> options;

}
