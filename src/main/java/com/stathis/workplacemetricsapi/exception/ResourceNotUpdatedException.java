package com.stathis.workplacemetricsapi.exception;

public class ResourceNotUpdatedException extends RuntimeException {

    public static final String RESOURCE_COULD_NOT_BE_UPDATED = "Resource could not be updated: ";

    public ResourceNotUpdatedException(String message) {
        super(message);
    }
}
