package app.repositories.exceptions;

public class AuthorIsNotOwnerException extends Throwable {
  public AuthorIsNotOwnerException(Long authorId) {
    super("Author is not: " + authorId);
  }
}
