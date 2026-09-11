package com.globaldocs.exception;

/**
 * Thrown when a document payload fails structural, format, or preliminary integrity checks.
 */
public class ValidationException extends GlobalDocsException {
    private final String fieldName;

    public ValidationException(String message) {
        super("VAL_FORMAT_ERR", message);
        this.fieldName = "general";
    }

    public ValidationException(String fieldName, String message) {
        super("VAL_FAIL_" + fieldName.toUpperCase(), message);
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}
