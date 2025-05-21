package com.recharge.app.exception;

@SuppressWarnings("serial")
public class EmailValidationException extends RuntimeException {
    public EmailValidationException(String message) {
        super(message);
    }
}