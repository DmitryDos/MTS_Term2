package app.exceptions;

public class UserNotFoundException extends Throwable {
  public UserNotFoundException(String errorMessage) {
    super(errorMessage);
  }
}
