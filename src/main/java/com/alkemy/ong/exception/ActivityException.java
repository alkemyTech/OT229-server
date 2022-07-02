package com.alkemy.ong.exception;

import java.io.IOException;

public class ActivityException extends IOException {

    public ActivityException(){
        super();
    }

    public ActivityException(String message) {
        super(message);
    }

    public ActivityException(String message, Throwable cause) {
        super(message, cause);
    }

    public ActivityException(Throwable cause) {
        super(cause);
    }
}
