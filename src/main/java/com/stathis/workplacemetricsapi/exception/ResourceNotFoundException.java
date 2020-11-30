package com.stathis.workplacemetricsapi.exception;

public class ResourceNotFoundException extends RuntimeException {

    public static final String RESOURCE_NOT_FOUND_FOR_ID = "Could not retrieve resource with id = ";

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
