package com.alkemy.ong.exception;

<<<<<<< HEAD
=======
import java.io.IOException;
>>>>>>> b78135dc29a47d94555123150688adc17bada3ae

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
