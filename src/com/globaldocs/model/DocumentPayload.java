package com.globaldocs.model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Represents the incoming enterprise document submitted to GlobalDocs for ingestion.
 * Carries all necessary payload data, country context, format, metadata and contents.
 */
public class DocumentPayload {
    private final String documentId;
    private final String title;
    private final Country country;
    private final DocumentType documentType;
    private final DocumentFormat format;
    private final String content;
    private final String taxIdentifier; // NIT (Colombia), RFC (Mexico), CUIT (Argentina), RUT (Chile)
    private final double amount;
    private final LocalDateTime submissionTimestamp;
    private final Map<String, String> metadata;

    public DocumentPayload(Builder builder) {
        this.documentId = builder.documentId != null ? builder.documentId : UUID.randomUUID().toString();
        this.title = builder.title;
        this.country = builder.country;
        this.documentType = builder.documentType;
        this.format = builder.format;
        this.content = builder.content;
        this.taxIdentifier = builder.taxIdentifier;
        this.amount = builder.amount;
        this.submissionTimestamp = builder.submissionTimestamp != null ? builder.submissionTimestamp : LocalDateTime.now();
        this.metadata = builder.metadata != null ? new HashMap<>(builder.metadata) : new HashMap<>();
    }

    public String getDocumentId() {
        return documentId;
    }

    public String getTitle() {
        return title;
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

    public String getContent() {
        return content;
    }

    public String getTaxIdentifier() {
        return taxIdentifier;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDateTime getSubmissionTimestamp() {
        return submissionTimestamp;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String documentId;
        private String title;
        private Country country;
        private DocumentType documentType;
        private DocumentFormat format;
        private String content = "";
        private String taxIdentifier;
        private double amount;
        private LocalDateTime submissionTimestamp;
        private Map<String, String> metadata = new HashMap<>();

        public Builder documentId(String documentId) {
            this.documentId = documentId;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
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

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder taxIdentifier(String taxIdentifier) {
            this.taxIdentifier = taxIdentifier;
            return this;
        }

        public Builder amount(double amount) {
            this.amount = amount;
            return this;
        }

        public Builder submissionTimestamp(LocalDateTime submissionTimestamp) {
            this.submissionTimestamp = submissionTimestamp;
            return this;
        }

        public Builder metadata(Map<String, String> metadata) {
            this.metadata = metadata;
            return this;
        }

        public Builder addMetadata(String key, String value) {
            if (this.metadata == null) {
                this.metadata = new HashMap<>();
            }
            this.metadata.put(key, value);
            return this;
        }

        public DocumentPayload build() {
            if (title == null || title.trim().isEmpty()) {
                title = "Doc-" + (documentType != null ? documentType.getCodePrefix() : "GEN") + "-" + System.currentTimeMillis();
            }
            return new DocumentPayload(this);
        }
    }
}
