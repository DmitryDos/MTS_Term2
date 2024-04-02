package app.repositories.exceptions;

public class TagNotFoundException extends Throwable {
  public TagNotFoundException(String errorMessage) {
    super(errorMessage);
  }
}
