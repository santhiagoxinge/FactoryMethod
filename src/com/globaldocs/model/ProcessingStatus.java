package com.globaldocs.model;

/**
 * Processing outcome status indicators.
 */
public enum ProcessingStatus {
    SUCCESS("Processed successfully with full regulatory compliance"),
    WARNING("Processed with non-critical regulatory warnings"),
    REJECTED("Rejected due to strict regulatory non-compliance"),
    FAILED("Execution failed due to format, parse, or structural errors");

    private final String description;

    ProcessingStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
