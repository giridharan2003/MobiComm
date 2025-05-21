package com.recharge.app.exception;

@SuppressWarnings("serial")
public class UsernameValidationException extends RuntimeException {
    public UsernameValidationException(String message) {
        super(message);
    }
}
