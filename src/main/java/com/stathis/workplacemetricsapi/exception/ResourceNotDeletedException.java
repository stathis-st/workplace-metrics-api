package com.stathis.workplacemetricsapi.exception;

public class ResourceNotDeletedException extends RuntimeException {

    public static final String RESOURCE_COULD_NOT_BE_DELETED = "There is no resource to be deleted with id = ";

    public ResourceNotDeletedException(String message) {
        super(message);
    }
}
