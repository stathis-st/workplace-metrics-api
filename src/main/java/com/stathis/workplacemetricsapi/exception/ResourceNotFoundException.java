package com.stathis.workplacemetricsapi.exception;

public class ResourceNotFoundException extends RuntimeException {

    public static final String RESOURCE_NOT_FOUND_WITH_ID = "Resource not found with id = ";

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
