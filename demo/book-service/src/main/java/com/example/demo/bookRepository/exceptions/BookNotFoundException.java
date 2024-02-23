package com.example.demo.bookRepository.exceptions;

public class BookNotFoundException extends Throwable {
  public BookNotFoundException(String errorMessage) {
    super(errorMessage);
  }
}
