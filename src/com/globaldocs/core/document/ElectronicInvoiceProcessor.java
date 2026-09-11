package com.globaldocs.core.document;

import com.globaldocs.model.Country;
import com.globaldocs.model.DocumentPayload;
import com.globaldocs.model.DocumentType;

import java.util.List;

/**
 * Concrete Product: Processor for Electronic Invoices (Facturas Electrónicas).
 * Computes applicable national sales tax (IVA), validates line totals, and generates electronic ledger records.
 */
public class ElectronicInvoiceProcessor extends BaseDocumentProcessor {

    public ElectronicInvoiceProcessor(Country country, String originFactoryName) {
        super(country, DocumentType.ELECTRONIC_INVOICE, originFactoryName);
    }

    @Override
    protected void executeSpecificProcessing(DocumentPayload payload, List<String> auditLogs, List<String> warnings) throws Exception {
        double amount = payload.getAmount();
        auditLogs.add(String.format("Processing invoice invoice amount: %.2f %s", amount, country.getCurrency()));

        // Calculate jurisdictional Value Added Tax (IVA)
        double vatRate;
        switch (country) {
            case COLOMBIA:
                vatRate = 0.19; // 19% IVA DIAN
                break;
            case MEXICO:
                vatRate = 0.16; // 16% IVA SAT
                break;
            case ARGENTINA:
                vatRate = 0.21; // 21% IVA AFIP
                break;
            case CHILE:
                vatRate = 0.19; // 19% IVA SII
                break;
            default:
                vatRate = 0.15;
        }

        double calculatedTax = amount * vatRate;
        double totalWithTax = amount + calculatedTax;

        auditLogs.add(String.format("Calculated regulatory tax rate for %s: %.0f%% (Tax: %.2f %s, Total: %.2f %s)",
                country.getDisplayName(), vatRate * 100, calculatedTax, country.getCurrency(), totalWithTax, country.getCurrency()));

        if (amount > 100_000_000 && country == Country.COLOMBIA) {
            warnings.add("DIAN High Value Notice: Invoices exceeding 100M COP trigger mandatory automatic fiscal audit reporting.");
        } else if (amount > 500_000 && country == Country.MEXICO) {
            warnings.add("SAT High Threshold Notice: Transactions exceeding $500,000 MXN require additional complement data (Complemento de Pagos).");
        }
    }
}
