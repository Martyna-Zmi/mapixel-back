package com.example.mapixelback.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZoneId;
import java.time.ZonedDateTime;
@ControllerAdvice
public class GlobalExceptionHandler {
    private final HttpStatus BAD_REQUEST = HttpStatus.BAD_REQUEST;
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @ExceptionHandler(value = {ResourceNotFoundException.class})
    public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException exception){
        ApiException apiException = new ApiException(
                exception.getMessage(),
                BAD_REQUEST,
                ZonedDateTime.now(ZoneId.of("Z"))
        );
        logger.warn("An exception occurred: 'Resource Not Found', but was handled correctly");
       return new ResponseEntity<>(apiException, BAD_REQUEST);
    }
    @ExceptionHandler(value = {InvalidDataException.class})
    public ResponseEntity<Object> handleInvalidDataException(InvalidDataException exception){
        ApiException apiException = new ApiException(
                exception.getMessage(),
                BAD_REQUEST,
                ZonedDateTime.now(ZoneId.of("Z"))
        );
        logger.warn("An exception occurred: 'Invalid Data', but was handled correctly");
        return new ResponseEntity<>(apiException, BAD_REQUEST);
    }
}
