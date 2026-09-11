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
 * Concrete Creator for Colombia (DIAN jurisdiction).
 * Implements the Factory Method createProcessor() to produce products
 * bound to Colombian fiscal and legal systems.
 */
public class ColombiaDocumentProcessorFactory extends DocumentProcessorFactory {

    public ColombiaDocumentProcessorFactory() {
        super(Country.COLOMBIA);
    }

    @Override
    protected DocumentProcessor createProcessor(DocumentType type) {
        if (type == null) return null;

        switch (type) {
            case ELECTRONIC_INVOICE:
                return new ElectronicInvoiceProcessor(Country.COLOMBIA, getFactoryName());
            case LEGAL_CONTRACT:
                return new LegalContractProcessor(Country.COLOMBIA, getFactoryName());
            case FINANCIAL_REPORT:
                return new FinancialReportProcessor(Country.COLOMBIA, getFactoryName());
            case DIGITAL_CERTIFICATE:
                return new DigitalCertificateProcessor(Country.COLOMBIA, getFactoryName());
            case TAX_DECLARATION:
                return new TaxDeclarationProcessor(Country.COLOMBIA, getFactoryName());
            default:
                throw new IllegalArgumentException("Unsupported document type in Colombia: " + type);
        }
    }
}
