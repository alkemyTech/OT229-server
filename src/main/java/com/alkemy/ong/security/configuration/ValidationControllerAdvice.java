package com.alkemy.ong.security.configuration;

import com.alkemy.ong.exception.CloudStorageClientException;
import com.alkemy.ong.exception.CorruptedFileException;
import com.alkemy.ong.exception.FileNotFoundOnCloudException;
import com.alkemy.ong.exception.PageIndexOutOfBoundsException;
import com.alkemy.ong.security.payload.ValidationErrorResponse;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

/**
 * This class catches and processes the exceptions thrown during a annotation-based validation process and generates
 * a default response.
 */
@Hidden
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

    @ExceptionHandler(CloudStorageClientException.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    @ResponseBody
    String onCloudStorageClientException(CloudStorageClientException e) {
        return "There was a problem with the cloud storage service. Problem is: " + e.getMessage();
    }

    @ExceptionHandler(CorruptedFileException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    String onCorruptedFileException(CorruptedFileException e) {
        return "Incompatible or corrupted image file.";
    }

    @ExceptionHandler(FileNotFoundOnCloudException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    String onFileNotFoundOnCloudException(FileNotFoundOnCloudException e) {
        return "The file doesn't exist.";
    }

    /**
     * Catches and processes the response for the exception thrown when the page number provided to perform a
     * paginated search is not valid.
     *
     * @param e the exception.
     * @return  the response body.
     */
    @ExceptionHandler(PageIndexOutOfBoundsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    String onPageIndexOutOfBoundsException(PageIndexOutOfBoundsException e) {
        return e.getMessage();
    }

    /**
     * Catches and processes the response for the exception thrown when a requested endpoint param is required but missing.
     *
     * @param e the exception.
     * @return  the response body.
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    String onMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        if (e.getMessage() != null) {
            return e.getMessage().substring(0, e.getMessage().lastIndexOf("'") + 1) + " missing.";
        }
        return "Missing request param.";
    }

    /**
     * Catches and processes the response for the exception thrown when a requested endpoint param is sent with a
     * different type than the one specified in the method declaration.
     *
     * @param e the exception.
     * @return  the response body.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    String onMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        return "Incorrect param type";
    }

}
