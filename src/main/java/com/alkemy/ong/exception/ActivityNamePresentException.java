package com.alkemy.ong.exception;


public class ActivityNamePresentException extends RuntimeException {

    public ActivityNamePresentException(){
        super();
    }

    public ActivityNamePresentException(String message) {
        super(message);
    }

    public ActivityNamePresentException(String message, Throwable cause) {
        super(message, cause);
    }

    public ActivityNamePresentException(Throwable cause) {
        super(cause);
    }
}
