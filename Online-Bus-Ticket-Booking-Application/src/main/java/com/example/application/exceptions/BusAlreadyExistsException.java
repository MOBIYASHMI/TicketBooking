package com.example.application.exceptions;

public class BusAlreadyExistsException extends RuntimeException {
    public BusAlreadyExistsException(String message) {
        super(message);
    }
}
