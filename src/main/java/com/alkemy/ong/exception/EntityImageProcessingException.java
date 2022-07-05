package com.alkemy.ong.exception;

/**
 * Parent class for all the Exceptions thrown during any part of the process to handle the entities' images.
 */
public class EntityImageProcessingException extends Exception {

    public EntityImageProcessingException() {
        super();
    }

    public EntityImageProcessingException(String message) {
        super(message);
    }

    public EntityImageProcessingException(String message, Throwable cause) {
        super(message, cause);
    }

    public EntityImageProcessingException(Throwable cause) {
        super(cause);
    }

    protected EntityImageProcessingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
