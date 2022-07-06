package com.alkemy.ong.exception;

public class CloudStorageClientException extends EntityImageProcessingException {

    public CloudStorageClientException() {
        super();
    }

    public CloudStorageClientException(String message) {
        super(message);
    }

    public CloudStorageClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public CloudStorageClientException(Throwable cause) {
        super(cause);
    }

    protected CloudStorageClientException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
