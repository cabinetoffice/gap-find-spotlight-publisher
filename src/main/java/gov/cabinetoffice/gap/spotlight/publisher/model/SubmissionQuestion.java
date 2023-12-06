package gov.cabinetoffice.gap.spotlight.publisher.model;

import gov.cabinetoffice.gap.spotlight.publisher.enums.ResponseTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmissionQuestion {
    private String questionId;

    private String profileField;

    private String fieldTitle;

    private String displayText;

    private String hintText;

    private String questionSuffix;

    private String fieldPrefix;

    private String adminSummary;

    private ResponseTypeEnum responseType;

    private SubmissionQuestionValidation validation;

    private String[] options;

    private String response;

    private String[] multiResponse;
}
