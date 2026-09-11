package com.globaldocs.core.validation;

import com.globaldocs.exception.ValidationException;
import com.globaldocs.model.DocumentPayload;

/**
 * Functional interface for document validation rules.
 */
@FunctionalInterface
public interface ValidationRule {
    void validate(DocumentPayload payload) throws ValidationException;
}
