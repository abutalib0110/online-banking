package com.example.banking.exception;

public class InvalidPhoneNumberException extends RuntimeException {
    public InvalidPhoneNumberException(String message) {
        super(message);
    }
}