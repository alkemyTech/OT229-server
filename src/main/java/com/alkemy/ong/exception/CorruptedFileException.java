package com.alkemy.ong.exception;

public class CorruptedFileException extends EntityImageProcessingException{

    public CorruptedFileException() {
        super();
    }

    public CorruptedFileException(String message) {
        super(message);
    }

    public CorruptedFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public CorruptedFileException(Throwable cause) {
        super(cause);
    }

    protected CorruptedFileException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
