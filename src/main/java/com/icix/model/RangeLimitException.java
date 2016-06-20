package com.icix.model;


public class RangeLimitException extends Exception {

    public RangeLimitException(String message) {
        super(message);
    }

    public RangeLimitException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
