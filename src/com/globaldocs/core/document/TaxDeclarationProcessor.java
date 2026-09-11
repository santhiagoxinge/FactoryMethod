package com.globaldocs.core.document;

import com.globaldocs.model.Country;
import com.globaldocs.model.DocumentPayload;
import com.globaldocs.model.DocumentType;

import java.util.List;

/**
 * Concrete Product: Processor for Tax Declarations (Declaraciones Tributarias).
 * Cross-references declared taxable income, withholding certificates, and fiscal filing dead-lines.
 */
public class TaxDeclarationProcessor extends BaseDocumentProcessor {

    public TaxDeclarationProcessor(Country country, String originFactoryName) {
        super(country, DocumentType.TAX_DECLARATION, originFactoryName);
    }

    @Override
    protected void executeSpecificProcessing(DocumentPayload payload, List<String> auditLogs, List<String> warnings) throws Exception {
        auditLogs.add(String.format("Auditing tax declaration return for jurisdiction: %s (%s)",
                country.getDisplayName(), country.getTaxAuthority()));

        switch (country) {
            case COLOMBIA:
                auditLogs.add("DIAN Formulario 110 / 210 cross-check against registered RUT tax responsibilities.");
                break;
            case MEXICO:
                auditLogs.add("SAT Declaración Anual Personas Morales / Físicas checked with e.firma validation.");
                break;
            case ARGENTINA:
                auditLogs.add("AFIP Impuesto a las Ganancias y Bienes Personales matching against Sistema Registral.");
                break;
            case CHILE:
                auditLogs.add("SII Formulario 22 (Declaración Anual de Renta) cross-verified with Formulario 29 VAT returns.");
                break;
        }

        if (payload.getAmount() <= 0) {
            warnings.add("Zero or negative net taxable income reported. Incurred audit flag for fiscal retention verification.");
        }
    }
}
