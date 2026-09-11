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
 * Concrete Creator for Chile (SII jurisdiction).
 * Implements the Factory Method createProcessor() to produce products
 * compliant with Chilean DTE and SII electronic invoicing resolutions.
 */
public class ChileDocumentProcessorFactory extends DocumentProcessorFactory {

    public ChileDocumentProcessorFactory() {
        super(Country.CHILE);
    }

    @Override
    protected DocumentProcessor createProcessor(DocumentType type) {
        if (type == null) return null;

        switch (type) {
            case ELECTRONIC_INVOICE:
                return new ElectronicInvoiceProcessor(Country.CHILE, getFactoryName());
            case LEGAL_CONTRACT:
                return new LegalContractProcessor(Country.CHILE, getFactoryName());
            case FINANCIAL_REPORT:
                return new FinancialReportProcessor(Country.CHILE, getFactoryName());
            case DIGITAL_CERTIFICATE:
                return new DigitalCertificateProcessor(Country.CHILE, getFactoryName());
            case TAX_DECLARATION:
                return new TaxDeclarationProcessor(Country.CHILE, getFactoryName());
            default:
                throw new IllegalArgumentException("Unsupported document type in Chile: " + type);
        }
    }
}
