package app.repositories.exceptions;

public class BookNotFoundException extends Throwable {
  public BookNotFoundException(String errorMessage) {
    super(errorMessage);
  }
}
