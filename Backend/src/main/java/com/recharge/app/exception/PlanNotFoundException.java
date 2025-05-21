package com.recharge.app.exception;

@SuppressWarnings("serial")
public class PlanNotFoundException extends RuntimeException {

    public PlanNotFoundException(String message) {
        super(message);
    }
}