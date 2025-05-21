package com.recharge.app.globalexception;

import com.recharge.app.exception.EmailValidationException;
import com.recharge.app.exception.MobileNumberValidationException;
import com.recharge.app.exception.NotFoundException;
import com.recharge.app.exception.OtpValidationException;
import com.recharge.app.exception.PlanNotFoundException;
import com.recharge.app.exception.UsernameValidationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<Map<String, Object>> generateErrorResponse(HttpStatus status, String error, String message) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", status.value());
        response.put("error", error);
        response.put("message", message);
        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(OtpValidationException.class)
    public ResponseEntity<Map<String, Object>> handleOtpException(OtpValidationException ex) {
        return generateErrorResponse(HttpStatus.BAD_REQUEST, "OTP Validation Failed", ex.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFoundException(NotFoundException ex) {
        return generateErrorResponse(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage());
    }

    @ExceptionHandler(MobileNumberValidationException.class)
    public ResponseEntity<Map<String, Object>> handleMobileNumberValidationException(MobileNumberValidationException ex) {
        return generateErrorResponse(HttpStatus.BAD_REQUEST, "Mobile Number Not Valid", ex.getMessage());
    }

    @ExceptionHandler(EmailValidationException.class)
    public ResponseEntity<Map<String, Object>> handleEmailValidationException(EmailValidationException ex) {
        return generateErrorResponse(HttpStatus.BAD_REQUEST, "Email Not Valid", ex.getMessage());
    }

    @ExceptionHandler(UsernameValidationException.class)
    public ResponseEntity<Map<String, Object>> handleUsernameValidationException(UsernameValidationException ex) {
        return generateErrorResponse(HttpStatus.BAD_REQUEST, "Username Not Valid", ex.getMessage());
    }

    @ExceptionHandler(PlanNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handlePlanNotFoundException(PlanNotFoundException ex) {
        return generateErrorResponse(HttpStatus.NOT_FOUND, "Plan Not Found", ex.getMessage());
    }

    // Handle General Exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        return generateErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", ex.getMessage());
    }
}
