package com.globaldocs.core.document;

import com.globaldocs.exception.GlobalDocsException;
import com.globaldocs.model.DocumentPayload;
import com.globaldocs.model.DocumentType;
import com.globaldocs.model.ProcessingResult;

/**
 * The Product interface in the Factory Method Pattern.
 * Defines the contract that all concrete document processors must fulfill.
 */
public interface DocumentProcessor {
    /**
     * Executes the complete processing pipeline for the given document payload.
     *
     * @param payload The incoming enterprise document metadata and content
     * @return ProcessingResult with execution metrics, regulatory stamps, and compliance status
     * @throws GlobalDocsException if unrecoverable error occurs
     */
    ProcessingResult process(DocumentPayload payload) throws GlobalDocsException;

    /**
     * Gets the document type handled by this processor.
     */
    DocumentType getHandledType();

    /**
     * Identifies the processor class name or registration moniker.
     */
    String getProcessorName();
}
