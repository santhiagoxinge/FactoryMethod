package com.globaldocs.model;

/**
 * Represents the supported countries in the GlobalDocs multinational platform.
 * Each country is linked to its tax/regulatory authority and national currency.
 */
public enum Country {
    COLOMBIA("Colombia", "DIAN", "COP", "Dirección de Impuestos y Aduanas Nacionales"),
    MEXICO("Mexico", "SAT", "MXN", "Servicio de Administración Tributaria"),
    ARGENTINA("Argentina", "AFIP", "ARS", "Administración Federal de Ingresos Públicos"),
    CHILE("Chile", "SII", "CLP", "Servicio de Impuestos Internos");

    private final String displayName;
    private final String taxAuthority;
    private final String currency;
    private final String authorityFullName;

    Country(String displayName, String taxAuthority, String currency, String authorityFullName) {
        this.displayName = displayName;
        this.taxAuthority = taxAuthority;
        this.currency = currency;
        this.authorityFullName = authorityFullName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getTaxAuthority() {
        return taxAuthority;
    }

    public String getCurrency() {
        return currency;
    }

    public String getAuthorityFullName() {
        return authorityFullName;
    }

    public static Country fromString(String text) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Country string cannot be null or empty");
        }
        String normalized = text.trim().replaceAll("[\"\'\\\\]", "").toUpperCase();
        for (Country country : values()) {
            if (country.name().equalsIgnoreCase(normalized) || country.displayName.equalsIgnoreCase(normalized)) {
                return country;
            }
        }
        throw new IllegalArgumentException("Unsupported country: " + text);
    }
}
