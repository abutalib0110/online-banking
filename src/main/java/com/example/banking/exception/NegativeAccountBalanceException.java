package com.example.banking.exception;

public class NegativeAccountBalanceException extends RuntimeException {
    public NegativeAccountBalanceException(String message) {
        super(message);
    }
}