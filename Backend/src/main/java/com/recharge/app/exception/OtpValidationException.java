package com.recharge.app.exception;

@SuppressWarnings("serial")
public class OtpValidationException extends RuntimeException {
    public OtpValidationException(String message) {
        super(message);
    }
}
