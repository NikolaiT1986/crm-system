package org.nikolait.crmsystem.exception.handler;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import org.nikolait.crmsystem.exception.InvalidTransactionException;
import org.nikolait.crmsystem.exception.InvalidSellerException;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(BindException.class)
    public ErrorResponse handleBindExceptions(BindException ex) {
        return ErrorResponse.builder(ex, ProblemDetail.forStatus(HttpStatus.BAD_REQUEST))
                .type(URI.create(ex.getClass().getSimpleName()))
                .property("errors", extractFieldErrors(ex))
                .build();
    }

    @ExceptionHandler(ValidationException.class)
    public ErrorResponse handleValidationException(ValidationException ex) {
        return ErrorResponse.builder(ex, HttpStatus.BAD_REQUEST, ex.getMessage())
                .type(URI.create(ex.getClass().getSimpleName()))
                .build();
    }

    @ExceptionHandler(PropertyReferenceException.class)
    public ErrorResponse handlePropertyReferenceException(PropertyReferenceException ex) {
        return ErrorResponse.builder(ex, HttpStatus.BAD_REQUEST, ex.getMessage())
                .type(URI.create(ex.getClass().getSimpleName()))
                .build();
    }

    @ExceptionHandler(HttpMessageConversionException.class)
    public ErrorResponse handleConversionException(HttpMessageConversionException ex) {
        return ErrorResponse.builder(ex, HttpStatus.BAD_REQUEST, ex.getMessage())
                .type(URI.create(ex.getClass().getSimpleName()))
                .build();
    }

    @ExceptionHandler(HttpMediaTypeException.class)
    public ErrorResponse handleMediaTypeException(HttpMediaTypeException ex) {
        return ErrorResponse.builder(ex, ex.getStatusCode(), ex.getMessage())
                .type(URI.create(ex.getClass().getSimpleName()))
                .build();
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ErrorResponse handleWalletNotFoundException(EntityNotFoundException ex) {
        return ErrorResponse.builder(ex, HttpStatus.NOT_FOUND, ex.getMessage())
                .type(URI.create(ex.getClass().getSimpleName()))
                .build();
    }

    @ExceptionHandler(InvalidSellerException.class)
    public ErrorResponse handleWalletNotFoundException(InvalidSellerException ex) {
        return ErrorResponse.builder(ex, HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage())
                .type(URI.create(ex.getClass().getSimpleName()))
                .build();
    }

    @ExceptionHandler(InvalidTransactionException.class)
    public ErrorResponse handleWalletNotFoundException(InvalidTransactionException ex) {
        return ErrorResponse.builder(ex, HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage())
                .type(URI.create(ex.getClass().getSimpleName()))
                .build();
    }

    private Map<String, String> extractFieldErrors(BindException ex) {
        return ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toUnmodifiableMap(
                        FieldError::getField,
                        error -> error.getDefaultMessage() != null
                                ? error.getDefaultMessage()
                                : ""
                ));
    }
}
