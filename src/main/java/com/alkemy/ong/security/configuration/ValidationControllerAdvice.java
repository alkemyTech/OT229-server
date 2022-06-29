package com.alkemy.ong.security.configuration;

import com.alkemy.ong.security.payload.ValidationErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

/**
 * This class catches and processes the exceptions thrown during a annotation-based validation process and generates
 * a default response.
 */
@ControllerAdvice
public class ValidationControllerAdvice {

    /**
     * Captures the exception thrown when an action would violate a constraint on repository structure, and returns
     * a proper response body.
     *
     * @param e the exception object thrown.
     * @return  the response body.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    ValidationErrorResponse onConstraintValidationException(ConstraintViolationException e) {
        return new ValidationErrorResponse(
                e.getConstraintViolations()
                        .stream()
                        .map(ConstraintViolation::getMessage)
                        .collect(Collectors.toList())
        );
    }

    /**
     * Captures the exception thrown when a simple method attribute, which was annotated for validation, is not valid,
     * and returns a proper response body.
     *
     * @param e the exception object thrown.
     * @return  the response body
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    ValidationErrorResponse onMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return new ValidationErrorResponse(
                e.getBindingResult().getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .collect(Collectors.toList())
        );
    }

    /**
     * Captures the exception thrown when a complex method attribute, which was annotated for validation in a method,
     * and possesses attributes marked for validation, and belongs to a class marked for validation, is not valid, and
     * returns a proper response body.
     *
     * @param e the exception object thrown.
     * @return  the response body
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    ValidationErrorResponse onBindException(BindException e) {
        return new ValidationErrorResponse(
                e.getBindingResult().getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .collect(Collectors.toList())
        );
    }

}
