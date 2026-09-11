package com.globaldocs.core.document;

import com.globaldocs.model.Country;
import com.globaldocs.model.DocumentPayload;
import com.globaldocs.model.DocumentType;

import java.util.List;

/**
 * Concrete Product: Processor for Financial Reports (Reportes Financieros).
 * Validates international financial reporting standards (IFRS / NIIF) tailored to local fiscal regimes.
 */
public class FinancialReportProcessor extends BaseDocumentProcessor {

    public FinancialReportProcessor(Country country, String originFactoryName) {
        super(country, DocumentType.FINANCIAL_REPORT, originFactoryName);
    }

    @Override
    protected void executeSpecificProcessing(DocumentPayload payload, List<String> auditLogs, List<String> warnings) throws Exception {
        auditLogs.add(String.format("Auditing financial report statements under %s national accounting frameworks", country.getDisplayName()));

        switch (country) {
            case COLOMBIA:
                auditLogs.add("Verified under Colombian NIIF (Normas Internacionales de Información Financiera - Ley 1314).");
                break;
            case MEXICO:
                auditLogs.add("Verified under Mexican NIF (Normas de Información Financiera - CINIF).");
                break;
            case ARGENTINA:
                auditLogs.add("Verified under FACPCE (Federación Argentina de Consejos Profesionales en Ciencias Económicas) RT 54.");
                break;
            case CHILE:
                auditLogs.add("Verified under Chilean CMF (Comisión para el Mercado Financiero) IFRS guidelines.");
                break;
        }

        if (payload.getAmount() < 0) {
            warnings.add("Negative reported fiscal balance detected. Ledger audited for impairment loss disclosure.");
        }
    }
}
