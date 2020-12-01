package com.stathis.workplacemetricsapi.controllers;

import com.stathis.workplacemetricsapi.exception.ResourceNotDeletedException;
import com.stathis.workplacemetricsapi.exception.ResourceNotFoundException;
import com.stathis.workplacemetricsapi.exception.ResourceNotUpdatedException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({ResourceNotFoundException.class,
            ResourceNotDeletedException.class,
            ResourceNotUpdatedException.class})
    public ResponseEntity<Object> handleNotFoundException(Exception exception, WebRequest request) {
        return new ResponseEntity<>(exception.getMessage(), new HttpHeaders(), HttpStatus.NOT_FOUND);
    }
}
