package com.globaldocs.model;

/**
 * Supported enterprise document types per workshop requirements:
 * 1. Electronic Invoices (Facturas Electrónicas)
 * 2. Legal Contracts (Contratos Legales)
 * 3. Financial Reports (Reportes Financieros)
 * 4. Digital Certificates (Certificados Digitales)
 * 5. Tax Declarations (Declaraciones Tributarias)
 */
public enum DocumentType {
    ELECTRONIC_INVOICE("Electronic Invoice", "Factura Electrónica", "INV"),
    LEGAL_CONTRACT("Legal Contract", "Contrato Legal", "CTR"),
    FINANCIAL_REPORT("Financial Report", "Reporte Financiero", "REP"),
    DIGITAL_CERTIFICATE("Digital Certificate", "Certificado Digital", "CRT"),
    TAX_DECLARATION("Tax Declaration", "Declaración Tributaria", "TAX");

    private final String englishName;
    private final String spanishName;
    private final String codePrefix;

    DocumentType(String englishName, String spanishName, String codePrefix) {
        this.englishName = englishName;
        this.spanishName = spanishName;
        this.codePrefix = codePrefix;
    }

    public String getEnglishName() {
        return englishName;
    }

    public String getSpanishName() {
        return spanishName;
    }

    public String getCodePrefix() {
        return codePrefix;
    }

    public static DocumentType fromString(String text) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("DocumentType string cannot be null or empty");
        }
        String clean = text.trim().replaceAll("[\"\'\\\\]", "");
        String normalized = clean.toUpperCase().replace("-", "_").replace(" ", "_");
        for (DocumentType type : values()) {
            if (type.name().equalsIgnoreCase(normalized) || 
                type.englishName.equalsIgnoreCase(clean) || 
                type.spanishName.equalsIgnoreCase(clean)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unsupported document type: " + text);
    }
}
