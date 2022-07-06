package com.alkemy.ong.exception;

public class FileNotFoundOnCloudException extends EntityImageProcessingException {

    public FileNotFoundOnCloudException() {
        super();
    }

    public FileNotFoundOnCloudException(String message) {
        super(message);
    }

    public FileNotFoundOnCloudException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileNotFoundOnCloudException(Throwable cause) {
        super(cause);
    }

    protected FileNotFoundOnCloudException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
