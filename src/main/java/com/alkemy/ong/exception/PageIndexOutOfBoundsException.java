package com.alkemy.ong.exception;

/**
 * Exception that indicates that the page number provided to perform a paginated search is not valid.
 *
 * This exception is caught by the Controller Advice and doesn't need handling.
 */
public class PageIndexOutOfBoundsException extends Exception {

    public PageIndexOutOfBoundsException() {
        super();
    }

    public PageIndexOutOfBoundsException(String message) {
        super(message);
    }

    public PageIndexOutOfBoundsException(String message, Throwable cause) {
        super(message, cause);
    }

    public PageIndexOutOfBoundsException(Throwable cause) {
        super(cause);
    }

}
