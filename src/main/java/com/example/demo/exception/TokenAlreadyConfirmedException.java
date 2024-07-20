package com.example.demo.exception;

public class TokenAlreadyConfirmedException extends RuntimeException {
    public TokenAlreadyConfirmedException(String message) {
        super(message);
    }
}
