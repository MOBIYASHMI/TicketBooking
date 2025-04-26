package com.example.application.exceptions;

public class SeatAlreadyBookedException extends RuntimeException {
  public SeatAlreadyBookedException(String message) {
    super(message);
  }
}
