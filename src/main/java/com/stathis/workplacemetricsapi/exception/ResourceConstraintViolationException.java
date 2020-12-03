package com.stathis.workplacemetricsapi.exception;

public class ResourceConstraintViolationException extends RuntimeException {

    public static final String SAVE_RESOURCE_CONSTRAINT_VIOLATION = "Could not save resource because of unique constraint violation.";
    public static final String UPDATE_RESOURCE_CONSTRAINT_VIOLATION = "Could not update resource because of unique constraint violation.";

    public ResourceConstraintViolationException(String message) {
        super(message);
    }
}
