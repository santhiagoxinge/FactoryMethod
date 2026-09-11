package com.globaldocs.exception;

import com.globaldocs.model.Country;

/**
 * Thrown when a document breaches country-specific regulatory decrees
 * (DIAN in Colombia, SAT in Mexico, AFIP in Argentina, SII in Chile).
 */
public class RegulatoryComplianceException extends GlobalDocsException {
    private final Country country;
    private final String regulatoryRule;

    public RegulatoryComplianceException(Country country, String regulatoryRule, String message) {
        super("REG_FAIL_" + (country != null ? country.getTaxAuthority() : "UNKNOWN"), message);
        this.country = country;
        this.regulatoryRule = regulatoryRule;
    }

    public Country getCountry() {
        return country;
    }

    public String getRegulatoryRule() {
        return regulatoryRule;
    }
}
