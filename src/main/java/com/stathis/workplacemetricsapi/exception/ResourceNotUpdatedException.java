package com.stathis.workplacemetricsapi.exception;

public class ResourceNotUpdatedException extends RuntimeException {

    public static final String COULD_NOT_BE_UPDATED = "Resource could not be updated: ";

    public ResourceNotUpdatedException() {
    }

    public ResourceNotUpdatedException(String message) {
        super(message);
    }

    public ResourceNotUpdatedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResourceNotUpdatedException(Throwable cause) {
        super(cause);
    }

    public ResourceNotUpdatedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
