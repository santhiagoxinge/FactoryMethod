package com.globaldocs.core.factory;

import com.globaldocs.core.document.DocumentProcessor;
import com.globaldocs.exception.GlobalDocsException;
import com.globaldocs.model.Country;
import com.globaldocs.model.DocumentPayload;
import com.globaldocs.model.DocumentType;
import com.globaldocs.model.ProcessingResult;

/**
 * The Abstract Creator class in the GoF Factory Method Pattern.
 * 
 * Declares the factory method:
 *   protected abstract DocumentProcessor createProcessor(DocumentType type);
 * 
 * Subclasses (ColombiaDocumentProcessorFactory, MexicoDocumentProcessorFactory,
 * ArgentinaDocumentProcessorFactory, ChileDocumentProcessorFactory) override this method
 * to instantiate jurisdiction-specific document processor products.
 */
public abstract class DocumentProcessorFactory {

    protected final Country country;

    public DocumentProcessorFactory(Country country) {
        this.country = country;
    }

    public Country getCountry() {
        return country;
    }

    public String getFactoryName() {
        return getClass().getSimpleName();
    }

    /**
     * The GoF Factory Method.
     * Concrete subclasses decide which concrete DocumentProcessor class to instantiate.
     *
     * @param type The document type to be handled
     * @return A concrete DocumentProcessor configured for this factory's country
     */
    protected abstract DocumentProcessor createProcessor(DocumentType type);

    /**
     * Orchestration / Template Method in the Creator class.
     * Uses the factory method to obtain the processor and invokes its business pipeline.
     *
     * @param payload The document payload to process
     * @return ProcessingResult containing status, stamps, and logs
     * @throws GlobalDocsException if an unrecoverable failure occurs
     */
    public ProcessingResult processDocument(DocumentPayload payload) throws GlobalDocsException {
        if (payload == null) {
            throw new IllegalArgumentException("Payload cannot be null");
        }

        // Invoke the Factory Method to obtain the appropriate product
        DocumentProcessor processor = createProcessor(payload.getDocumentType());
        if (processor == null) {
            throw new GlobalDocsException("NO_PROCESSOR", 
                String.format("Factory %s could not create a processor for type %s", 
                    getFactoryName(), payload.getDocumentType()));
        }

        // Delegate execution to the product
        return processor.process(payload);
    }
}
