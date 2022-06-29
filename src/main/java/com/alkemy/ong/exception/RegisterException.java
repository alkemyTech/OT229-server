package com.alkemy.ong.exception;

import java.io.IOException;

public class RegisterException extends IOException {

    public RegisterException(){
        super();
    }

    public RegisterException(String message) {
        super(message);
    }

    public RegisterException(String message, Throwable cause) {
        super(message, cause);
    }

    public RegisterException(Throwable cause) {
        super(cause);
    }
}
