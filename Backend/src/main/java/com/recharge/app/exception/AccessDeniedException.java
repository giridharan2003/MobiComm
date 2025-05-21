package com.recharge.app.exception;

@SuppressWarnings("serial")
public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException(String message) {
        super(message);
    }
}
