package com.example.demo.repositories.exceptions;

public class BookNotFoundException extends Throwable {
  public BookNotFoundException(String errorMessage) {
    super(errorMessage);
  }
}
