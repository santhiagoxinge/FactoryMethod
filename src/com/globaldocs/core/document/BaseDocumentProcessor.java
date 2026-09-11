package com.globaldocs.core.document;

import com.globaldocs.core.validation.FormatComplianceValidator;
import com.globaldocs.core.validation.RegulatoryComplianceEngine;
import com.globaldocs.exception.GlobalDocsException;
import com.globaldocs.exception.RegulatoryComplianceException;
import com.globaldocs.exception.ValidationException;
import com.globaldocs.model.Country;
import com.globaldocs.model.DocumentPayload;
import com.globaldocs.model.DocumentType;
import com.globaldocs.model.ProcessingResult;
import com.globaldocs.model.ProcessingStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract Base Product providing template execution steps and common auditing
 * for all concrete document processors.
 */
public abstract class BaseDocumentProcessor implements DocumentProcessor {

    protected final Country country;
    protected final DocumentType documentType;
    protected final String originFactoryName;

    public BaseDocumentProcessor(Country country, DocumentType documentType, String originFactoryName) {
        this.country = country;
        this.documentType = documentType;
        this.originFactoryName = originFactoryName;
    }

    @Override
    public DocumentType getHandledType() {
        return documentType;
    }

    @Override
    public String getProcessorName() {
        return getClass().getSimpleName();
    }

    @Override
    public ProcessingResult process(DocumentPayload payload) throws GlobalDocsException {
        long startTime = System.currentTimeMillis();
        List<String> auditLogs = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        auditLogs.add(String.format("Initializing %s for document [%s] targeted at %s", 
                getProcessorName(), payload.getDocumentId(), payload.getCountry()));

        // 1. Basic Payload Validation
        if (payload.getTitle() == null || payload.getTitle().trim().isEmpty()) {
            throw new ValidationException("title", "Document title cannot be empty");
        }

        // 2. Format Compliance Check (.pdf, .doc, .docx, .md, .csv, .txt, .xlsx)
        try {
            FormatComplianceValidator.validateFormat(payload);
            auditLogs.add(String.format("Format verification passed: .%s is certified for %s in %s",
                    payload.getFormat().getExtension(), payload.getDocumentType().getEnglishName(), payload.getCountry()));
        } catch (ValidationException ve) {
            auditLogs.add("Format verification failed: " + ve.getMessage());
            errors.add(ve.getMessage());
            return buildErrorResult(payload, startTime, ProcessingStatus.FAILED, "N/A", 
                    "Format Validation Error: " + ve.getMessage(), auditLogs, warnings, errors);
        }

        // 3. Country Regulatory Compliance Check
        try {
            RegulatoryComplianceEngine.validateCountryCompliance(payload);
            auditLogs.add(String.format("Regulatory compliance verified with %s authority standards",
                    payload.getCountry().getTaxAuthority()));
        } catch (RegulatoryComplianceException rce) {
            auditLogs.add(String.format("Regulatory rejection by %s: %s", rce.getCountry().getTaxAuthority(), rce.getMessage()));
            errors.add(rce.getMessage());
            return buildErrorResult(payload, startTime, ProcessingStatus.REJECTED, "REJECTED-" + rce.getRegulatoryRule(), 
                    "Regulatory Rejection: " + rce.getMessage(), auditLogs, warnings, errors);
        }

        // 4. Concrete Product Specific Processing
        try {
            executeSpecificProcessing(payload, auditLogs, warnings);
        } catch (Exception e) {
            auditLogs.add("Domain processing error: " + e.getMessage());
            errors.add(e.getMessage());
            return buildErrorResult(payload, startTime, ProcessingStatus.FAILED, "ERR-EXEC", 
                    "Processing Error: " + e.getMessage(), auditLogs, warnings, errors);
        }

        // 5. Official Regulatory Stamping
        String stamp = RegulatoryComplianceEngine.generateRegulatoryStamp(payload);
        auditLogs.add(String.format("Official %s regulatory stamp applied: %s", payload.getCountry().getTaxAuthority(), stamp));

        long duration = System.currentTimeMillis() - startTime;
        ProcessingStatus finalStatus = warnings.isEmpty() ? ProcessingStatus.SUCCESS : ProcessingStatus.WARNING;

        ProcessingResult.Builder builder = ProcessingResult.builder()
                .documentId(payload.getDocumentId())
                .documentTitle(payload.getTitle())
                .country(payload.getCountry())
                .documentType(payload.getDocumentType())
                .format(payload.getFormat())
                .status(finalStatus)
                .regulatoryStamp(stamp)
                .authorityValidationMessage(String.format("Certified by %s (%s)", payload.getCountry().getTaxAuthority(), payload.getCountry().getDisplayName()))
                .factoryUsed(originFactoryName)
                .processorUsed(getProcessorName())
                .processingTimeMs(duration)
                .completedAt(LocalDateTime.now());

        for (String log : auditLogs) {
            builder.addAuditLog(log);
        }
        for (String w : warnings) {
            builder.addWarning(w);
        }

        return builder.build();
    }

    /**
     * Hook method implemented by concrete products to enforce type-specific domain rules.
     */
    protected abstract void executeSpecificProcessing(DocumentPayload payload, List<String> auditLogs, List<String> warnings) throws Exception;

    private ProcessingResult buildErrorResult(DocumentPayload payload, long startTime, ProcessingStatus status,
                                              String stamp, String message, List<String> auditLogs,
                                              List<String> warnings, List<String> errors) {
        long duration = System.currentTimeMillis() - startTime;
        ProcessingResult.Builder builder = ProcessingResult.builder()
                .documentId(payload.getDocumentId())
                .documentTitle(payload.getTitle())
                .country(payload.getCountry())
                .documentType(payload.getDocumentType())
                .format(payload.getFormat())
                .status(status)
                .regulatoryStamp(stamp)
                .authorityValidationMessage(message)
                .factoryUsed(originFactoryName)
                .processorUsed(getProcessorName())
                .processingTimeMs(duration)
                .completedAt(LocalDateTime.now());

        for (String log : auditLogs) builder.addAuditLog(log);
        for (String w : warnings) builder.addWarning(w);
        for (String e : errors) builder.addError(e);

        return builder.build();
    }
}
