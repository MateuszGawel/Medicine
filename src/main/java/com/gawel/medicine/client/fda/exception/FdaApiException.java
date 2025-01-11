package com.gawel.medicine.client.fda.exception;

public class FdaApiException extends RuntimeException {
    public FdaApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public FdaApiException(String message) {
        super(message);
    }
}
