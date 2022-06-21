package com.alkemy.ong.security.configuration;


import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class ErrorHandlingControllerAdvice {
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    List<String> onConstraintValidationException(ConstraintViolationException e) {

        List<String> errorList = new ArrayList<>();
        for (ConstraintViolation violation : e.getConstraintViolations()) {
            errorList.add("Error: " + violation.getPropertyPath().toString() + ": " + violation.getMessage());
        }
        return errorList;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    List<String> onMethodArgumentNotValidException(MethodArgumentNotValidException e) {

        List<String> errorList = new ArrayList<>();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            errorList.add("Error: " + fieldError.getField() + ": " + fieldError.getDefaultMessage());
        }
        return errorList;
    }
}
