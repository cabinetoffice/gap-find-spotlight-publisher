package gov.cabinetoffice.gap.spotlight.publisher.costants;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.Map;

public class ValidationMaps {

    public static final Map<String, Object> SHORT_ANSWER_VALIDATION = Map.ofEntries(
            new AbstractMap.SimpleEntry<String, Object>("minLength", 1),
            new AbstractMap.SimpleEntry<String, Object>("maxLength", 250));

    public static final Map<String, Object> LONG_ANSWER_VALIDATION = Map.ofEntries(
            new AbstractMap.SimpleEntry<String, Object>("minLength", 2),
            new AbstractMap.SimpleEntry<String, Object>("maxLength", 6000));

    public static final Map<String, Object> NUMERIC_ANSWER_VALIDATION = Map
            .ofEntries(new AbstractMap.SimpleEntry<String, Object>("greaterThanZero", true));

    public static final Map<String, Object> SINGLE_FILE_UPLOAD_VALIDATION = Map.ofEntries(
            new AbstractMap.SimpleEntry<String, Object>("maxFileSizeMB", 300),
            new AbstractMap.SimpleEntry<String, Object>("allowedTypes",
                    new String[]{"DOC", "DOCX", "ODT", "PDF", "XLS", "XLSX", "ZIP"}));

    public static final Map<String, Object> NO_VALIDATION = Collections.emptyMap();

}
