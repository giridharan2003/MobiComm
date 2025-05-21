package com.recharge.app.exception;

@SuppressWarnings("serial")
public class MobileNumberValidationException extends RuntimeException {
    public MobileNumberValidationException(String message) {
        super(message);
    }
}