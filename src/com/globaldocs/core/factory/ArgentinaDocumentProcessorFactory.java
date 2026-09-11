package com.globaldocs.core.factory;

import com.globaldocs.core.document.DigitalCertificateProcessor;
import com.globaldocs.core.document.DocumentProcessor;
import com.globaldocs.core.document.ElectronicInvoiceProcessor;
import com.globaldocs.core.document.FinancialReportProcessor;
import com.globaldocs.core.document.LegalContractProcessor;
import com.globaldocs.core.document.TaxDeclarationProcessor;
import com.globaldocs.model.Country;
import com.globaldocs.model.DocumentType;

/**
 * Concrete Creator for Argentina (AFIP jurisdiction).
 * Implements the Factory Method createProcessor() to produce products
 * conforming to AFIP resolution and Argentine commercial codes.
 */
public class ArgentinaDocumentProcessorFactory extends DocumentProcessorFactory {

    public ArgentinaDocumentProcessorFactory() {
        super(Country.ARGENTINA);
    }

    @Override
    protected DocumentProcessor createProcessor(DocumentType type) {
        if (type == null) return null;

        switch (type) {
            case ELECTRONIC_INVOICE:
                return new ElectronicInvoiceProcessor(Country.ARGENTINA, getFactoryName());
            case LEGAL_CONTRACT:
                return new LegalContractProcessor(Country.ARGENTINA, getFactoryName());
            case FINANCIAL_REPORT:
                return new FinancialReportProcessor(Country.ARGENTINA, getFactoryName());
            case DIGITAL_CERTIFICATE:
                return new DigitalCertificateProcessor(Country.ARGENTINA, getFactoryName());
            case TAX_DECLARATION:
                return new TaxDeclarationProcessor(Country.ARGENTINA, getFactoryName());
            default:
                throw new IllegalArgumentException("Unsupported document type in Argentina: " + type);
        }
    }
}
