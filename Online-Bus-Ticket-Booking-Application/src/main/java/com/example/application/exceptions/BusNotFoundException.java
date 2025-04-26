package com.example.application.exceptions;

public class BusNotFoundException extends RuntimeException{
    public BusNotFoundException(String message) {
        super(message);
    }
}
