package com.globaldocs.core.validation;

import com.globaldocs.exception.ValidationException;
import com.globaldocs.model.Country;
import com.globaldocs.model.DocumentFormat;
import com.globaldocs.model.DocumentPayload;
import com.globaldocs.model.DocumentType;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Validates document format compliance against enterprise requirements and country rules.
 * Supports all 7 formats: .pdf, .doc, .docx, .md, .csv, .txt, .xlsx.
 */
public class FormatComplianceValidator {

    private static final Map<DocumentType, Set<DocumentFormat>> ALLOWED_FORMATS_BY_TYPE = new HashMap<>();

    static {
        // Electronic Invoices: Structured or official archival formats
        ALLOWED_FORMATS_BY_TYPE.put(DocumentType.ELECTRONIC_INVOICE, 
                EnumSet.of(DocumentFormat.PDF, DocumentFormat.XLSX, DocumentFormat.CSV, DocumentFormat.TXT));

        // Legal Contracts: Rich text or archival documents
        ALLOWED_FORMATS_BY_TYPE.put(DocumentType.LEGAL_CONTRACT, 
                EnumSet.of(DocumentFormat.PDF, DocumentFormat.DOC, DocumentFormat.DOCX, DocumentFormat.TXT, DocumentFormat.MD));

        // Financial Reports: Quantitative spread or report formats
        ALLOWED_FORMATS_BY_TYPE.put(DocumentType.FINANCIAL_REPORT, 
                EnumSet.of(DocumentFormat.PDF, DocumentFormat.XLSX, DocumentFormat.CSV, DocumentFormat.DOCX));

        // Digital Certificates: Archival or text-encoded certificate formats
        ALLOWED_FORMATS_BY_TYPE.put(DocumentType.DIGITAL_CERTIFICATE, 
                EnumSet.of(DocumentFormat.PDF, DocumentFormat.TXT, DocumentFormat.MD));

        // Tax Declarations: Official fiscal or table formats
        ALLOWED_FORMATS_BY_TYPE.put(DocumentType.TAX_DECLARATION, 
                EnumSet.of(DocumentFormat.PDF, DocumentFormat.XLSX, DocumentFormat.CSV, DocumentFormat.TXT));
    }

    public static void validateFormat(DocumentPayload payload) throws ValidationException {
        if (payload == null) {
            throw new ValidationException("payload", "Document payload cannot be null");
        }

        DocumentFormat format = payload.getFormat();
        if (format == null) {
            throw new ValidationException("format", "Document format must be specified");
        }

        DocumentType type = payload.getDocumentType();
        if (type == null) {
            throw new ValidationException("documentType", "Document type must be specified");
        }

        Set<DocumentFormat> allowed = ALLOWED_FORMATS_BY_TYPE.get(type);
        if (allowed != null && !allowed.contains(format)) {
            throw new ValidationException("format", 
                String.format("Format .%s is incompatible with document type '%s'. Allowed formats: %s",
                    format.getExtension(), type.getEnglishName(), allowed));
        }

        // Country-specific format strictness
        Country country = payload.getCountry();
        if (country == Country.COLOMBIA && type == DocumentType.ELECTRONIC_INVOICE && format == DocumentFormat.TXT) {
            throw new ValidationException("format", "DIAN (Colombia) requires PDF or structured CSV/XLSX for invoices, plain TXT is not permitted.");
        }
        if (country == Country.MEXICO && type == DocumentType.ELECTRONIC_INVOICE && format == DocumentFormat.CSV) {
            throw new ValidationException("format", "SAT (Mexico) CFDI standard requires PDF or XLSX format for fiscal accounting representation.");
        }
    }

    public static boolean isFormatSupported(DocumentType type, DocumentFormat format) {
        Set<DocumentFormat> set = ALLOWED_FORMATS_BY_TYPE.get(type);
        return set != null && set.contains(format);
    }
}
