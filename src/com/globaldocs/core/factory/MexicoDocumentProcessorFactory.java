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
 * Concrete Creator for Mexico (SAT jurisdiction).
 * Implements the Factory Method createProcessor() to produce products
 * bound to Mexican CFDI 4.0 and commercial legislation.
 */
public class MexicoDocumentProcessorFactory extends DocumentProcessorFactory {

    public MexicoDocumentProcessorFactory() {
        super(Country.MEXICO);
    }

    @Override
    protected DocumentProcessor createProcessor(DocumentType type) {
        if (type == null) return null;

        switch (type) {
            case ELECTRONIC_INVOICE:
                return new ElectronicInvoiceProcessor(Country.MEXICO, getFactoryName());
            case LEGAL_CONTRACT:
                return new LegalContractProcessor(Country.MEXICO, getFactoryName());
            case FINANCIAL_REPORT:
                return new FinancialReportProcessor(Country.MEXICO, getFactoryName());
            case DIGITAL_CERTIFICATE:
                return new DigitalCertificateProcessor(Country.MEXICO, getFactoryName());
            case TAX_DECLARATION:
                return new TaxDeclarationProcessor(Country.MEXICO, getFactoryName());
            default:
                throw new IllegalArgumentException("Unsupported document type in Mexico: " + type);
        }
    }
}
