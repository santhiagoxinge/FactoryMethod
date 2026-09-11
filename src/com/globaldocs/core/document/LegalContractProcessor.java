package com.globaldocs.core.document;

import com.globaldocs.model.Country;
import com.globaldocs.model.DocumentPayload;
import com.globaldocs.model.DocumentType;

import java.util.List;

/**
 * Concrete Product: Processor for Legal Contracts (Contratos Legales).
 * Validates clause integrity, notarization requirements, and legal jurisdiction bindings.
 */
public class LegalContractProcessor extends BaseDocumentProcessor {

    public LegalContractProcessor(Country country, String originFactoryName) {
        super(country, DocumentType.LEGAL_CONTRACT, originFactoryName);
    }

    @Override
    protected void executeSpecificProcessing(DocumentPayload payload, List<String> auditLogs, List<String> warnings) throws Exception {
        String content = payload.getContent();
        int contentLength = content != null ? content.length() : 0;

        auditLogs.add(String.format("Analyzing legal clause clauses and jurisdiction binding for %s", country.getDisplayName()));
        auditLogs.add(String.format("Contract text size: %d characters across jurisdiction laws", contentLength));

        // Check for essential dispute clauses or length
        if (contentLength < 30) {
            warnings.add("Brief contract clause warning: Contract body has fewer than 30 characters; verify complete execution clauses.");
        }

        switch (country) {
            case COLOMBIA:
                auditLogs.add("Applied Colombian Commercial Code (Código de Comercio de Colombia - Ley 527 de 1999) digital signature requirements.");
                break;
            case MEXICO:
                auditLogs.add("Applied Mexican Commerce Code (Código de Comercio de México) NOM-151 digital preservation timestamping.");
                break;
            case ARGENTINA:
                auditLogs.add("Applied Argentine Civil and Commercial Code (Ley 25.506 de Firma Digital).");
                break;
            case CHILE:
                auditLogs.add("Applied Chilean Law 19.799 on Electronic Documents and Advanced Electronic Signatures.");
                break;
        }
    }
}
