package com.globaldocs.exception;

/**
 * Base custom exception for GlobalDocs processing pipeline.
 */
public class GlobalDocsException extends Exception {
    private final String errorCode;

    public GlobalDocsException(String message) {
        super(message);
        this.errorCode = "GLOBAL_ERR";
    }

    public GlobalDocsException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public GlobalDocsException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
