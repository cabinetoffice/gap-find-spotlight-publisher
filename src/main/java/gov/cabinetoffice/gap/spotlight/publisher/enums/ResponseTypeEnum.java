package gov.cabinetoffice.gap.spotlight.publisher.enums;

import java.util.Map;

import static gov.cabinetoffice.gap.spotlight.publisher.costants.ValidationMaps.LONG_ANSWER_VALIDATION;
import static gov.cabinetoffice.gap.spotlight.publisher.costants.ValidationMaps.NO_VALIDATION;
import static gov.cabinetoffice.gap.spotlight.publisher.costants.ValidationMaps.NUMERIC_ANSWER_VALIDATION;
import static gov.cabinetoffice.gap.spotlight.publisher.costants.ValidationMaps.SHORT_ANSWER_VALIDATION;
import static gov.cabinetoffice.gap.spotlight.publisher.costants.ValidationMaps.SINGLE_FILE_UPLOAD_VALIDATION;


public enum ResponseTypeEnum {

    // TODO update validation on AddressInput and Date
    YesNo(NO_VALIDATION), SingleSelection(NO_VALIDATION), Dropdown(NO_VALIDATION), MultipleSelection(NO_VALIDATION),
    ShortAnswer(SHORT_ANSWER_VALIDATION), LongAnswer(LONG_ANSWER_VALIDATION), AddressInput(NO_VALIDATION),
    Numeric(NUMERIC_ANSWER_VALIDATION), Date(NO_VALIDATION), SingleFileUpload(SINGLE_FILE_UPLOAD_VALIDATION);

    private final Map<String, Object> validation;

    ResponseTypeEnum(Map<String, Object> validation) {
        this.validation = validation;
    }

    public Map<String, Object> getValidation() {
        return validation;
    }

}
