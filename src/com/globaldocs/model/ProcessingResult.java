package com.globaldocs.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates the complete result of processing a document through the Factory Method pipeline.
 * Contains regulatory verification stamps (CUFE, UUID, CAE, DTE), processing times,
 * factory invoked, concrete processor executed, and compliance audit trail.
 */
public class ProcessingResult {
    private final String documentId;
    private final String documentTitle;
    private final Country country;
    private final DocumentType documentType;
    private final DocumentFormat format;
    private final ProcessingStatus status;
    private final String regulatoryStamp; // CUFE (Colombia), UUID/Timbre (Mexico), CAE (Argentina), DTE Folio (Chile)
    private final String authorityValidationMessage;
    private final String factoryUsed;
    private final String processorUsed;
    private final long processingTimeMs;
    private final LocalDateTime completedAt;
    private final List<String> auditLogs;
    private final List<String> warnings;
    private final List<String> errors;

    public ProcessingResult(Builder builder) {
        this.documentId = builder.documentId;
        this.documentTitle = builder.documentTitle;
        this.country = builder.country;
        this.documentType = builder.documentType;
        this.format = builder.format;
        this.status = builder.status;
        this.regulatoryStamp = builder.regulatoryStamp;
        this.authorityValidationMessage = builder.authorityValidationMessage;
        this.factoryUsed = builder.factoryUsed;
        this.processorUsed = builder.processorUsed;
        this.processingTimeMs = builder.processingTimeMs;
        this.completedAt = builder.completedAt != null ? builder.completedAt : LocalDateTime.now();
        this.auditLogs = builder.auditLogs != null ? new ArrayList<>(builder.auditLogs) : new ArrayList<>();
        this.warnings = builder.warnings != null ? new ArrayList<>(builder.warnings) : new ArrayList<>();
        this.errors = builder.errors != null ? new ArrayList<>(builder.errors) : new ArrayList<>();
    }

    public String getDocumentId() {
        return documentId;
    }

    public String getDocumentTitle() {
        return documentTitle;
    }

    public Country getCountry() {
        return country;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public DocumentFormat getFormat() {
        return format;
    }

    public ProcessingStatus getStatus() {
        return status;
    }

    public String getRegulatoryStamp() {
        return regulatoryStamp;
    }

    public String getAuthorityValidationMessage() {
        return authorityValidationMessage;
    }

    public String getFactoryUsed() {
        return factoryUsed;
    }

    public String getProcessorUsed() {
        return processorUsed;
    }

    public long getProcessingTimeMs() {
        return processingTimeMs;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public List<String> getAuditLogs() {
        return auditLogs;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public List<String> getErrors() {
        return errors;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String documentId;
        private String documentTitle;
        private Country country;
        private DocumentType documentType;
        private DocumentFormat format;
        private ProcessingStatus status = ProcessingStatus.SUCCESS;
        private String regulatoryStamp = "N/A";
        private String authorityValidationMessage = "Validation passed";
        private String factoryUsed = "UnknownFactory";
        private String processorUsed = "UnknownProcessor";
        private long processingTimeMs;
        private LocalDateTime completedAt;
        private List<String> auditLogs = new ArrayList<>();
        private List<String> warnings = new ArrayList<>();
        private List<String> errors = new ArrayList<>();

        public Builder documentId(String documentId) {
            this.documentId = documentId;
            return this;
        }

        public Builder documentTitle(String documentTitle) {
            this.documentTitle = documentTitle;
            return this;
        }

        public Builder country(Country country) {
            this.country = country;
            return this;
        }

        public Builder documentType(DocumentType documentType) {
            this.documentType = documentType;
            return this;
        }

        public Builder format(DocumentFormat format) {
            this.format = format;
            return this;
        }

        public Builder status(ProcessingStatus status) {
            this.status = status;
            return this;
        }

        public Builder regulatoryStamp(String regulatoryStamp) {
            this.regulatoryStamp = regulatoryStamp;
            return this;
        }

        public Builder authorityValidationMessage(String message) {
            this.authorityValidationMessage = message;
            return this;
        }

        public Builder factoryUsed(String factoryUsed) {
            this.factoryUsed = factoryUsed;
            return this;
        }

        public Builder processorUsed(String processorUsed) {
            this.processorUsed = processorUsed;
            return this;
        }

        public Builder processingTimeMs(long processingTimeMs) {
            this.processingTimeMs = processingTimeMs;
            return this;
        }

        public Builder completedAt(LocalDateTime completedAt) {
            this.completedAt = completedAt;
            return this;
        }

        public Builder addAuditLog(String log) {
            this.auditLogs.add(log);
            return this;
        }

        public Builder addWarning(String warning) {
            this.warnings.add(warning);
            return this;
        }

        public Builder addError(String error) {
            this.errors.add(error);
            return this;
        }

        public ProcessingResult build() {
            return new ProcessingResult(this);
        }
    }
}
