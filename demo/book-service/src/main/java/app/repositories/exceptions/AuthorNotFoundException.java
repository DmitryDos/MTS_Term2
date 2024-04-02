package app.repositories.exceptions;

public class AuthorNotFoundException extends Throwable {
  public AuthorNotFoundException(String errorMessage) {
    super(errorMessage);
  }
}
