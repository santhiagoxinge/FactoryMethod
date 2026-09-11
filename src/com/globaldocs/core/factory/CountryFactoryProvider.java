package com.globaldocs.core.factory;

import com.globaldocs.model.Country;

import java.util.EnumMap;
import java.util.Map;

/**
 * Registry and Provider for country-specific DocumentProcessorFactory creators.
 * Encapsulates the selection of the appropriate concrete factory based on target country.
 */
public class CountryFactoryProvider {

    private static final Map<Country, DocumentProcessorFactory> FACTORIES = new EnumMap<>(Country.class);

    static {
        FACTORIES.put(Country.COLOMBIA, new ColombiaDocumentProcessorFactory());
        FACTORIES.put(Country.MEXICO, new MexicoDocumentProcessorFactory());
        FACTORIES.put(Country.ARGENTINA, new ArgentinaDocumentProcessorFactory());
        FACTORIES.put(Country.CHILE, new ChileDocumentProcessorFactory());
    }

    /**
     * Obtains the concrete Factory Method Creator for the given country.
     *
     * @param country The target country
     * @return The concrete DocumentProcessorFactory implementation
     */
    public static DocumentProcessorFactory getFactory(Country country) {
        if (country == null) {
            throw new IllegalArgumentException("Country cannot be null");
        }
        DocumentProcessorFactory factory = FACTORIES.get(country);
        if (factory == null) {
            throw new IllegalStateException("No factory registered for country: " + country);
        }
        return factory;
    }

    /**
     * Allows dynamic registration of custom or mock factories for testing or expansion.
     */
    public static void registerFactory(Country country, DocumentProcessorFactory factory) {
        if (country == null || factory == null) {
            throw new IllegalArgumentException("Country and Factory must not be null");
        }
        FACTORIES.put(country, factory);
    }
}
