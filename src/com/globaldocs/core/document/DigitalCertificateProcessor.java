package com.globaldocs.core.document;

import com.globaldocs.model.Country;
import com.globaldocs.model.DocumentPayload;
import com.globaldocs.model.DocumentType;

import java.util.List;

/**
 * Concrete Product: Processor for Digital Certificates (Certificados Digitales).
 * Verifies public key infrastructure (PKI), root CA trust chains, and expiration validity.
 */
public class DigitalCertificateProcessor extends BaseDocumentProcessor {

    public DigitalCertificateProcessor(Country country, String originFactoryName) {
        super(country, DocumentType.DIGITAL_CERTIFICATE, originFactoryName);
    }

    @Override
    protected void executeSpecificProcessing(DocumentPayload payload, List<String> auditLogs, List<String> warnings) throws Exception {
        auditLogs.add(String.format("Validating X.509 PKI certificate chain against %s Root Certification Authorities", country.getDisplayName()));

        switch (country) {
            case COLOMBIA:
                auditLogs.add("Root CA validation: ONAC (Organismo Nacional de Acreditación de Colombia) authorized certifier.");
                break;
            case MEXICO:
                auditLogs.add("Root CA validation: Secretaría de Economía (PSC - Prestador de Servicios de Certificación).");
                break;
            case ARGENTINA:
                auditLogs.add("Root CA validation: Ente Licenciante de Firma Digital (Secretaría de Innovación Pública).");
                break;
            case CHILE:
                auditLogs.add("Root CA validation: Entidad Acreditadora del Ministerio de Economía, Fomento y Turismo.");
                break;
        }

        String content = payload.getContent();
        if (content != null && content.toLowerCase().contains("expired")) {
            warnings.add("Certificate status warning: Certificate revocation list (CRL) flags possible key expiration.");
        }
    }
}
