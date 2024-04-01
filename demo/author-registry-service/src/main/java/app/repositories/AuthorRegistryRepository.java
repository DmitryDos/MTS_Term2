package app.repositories;

import app.entities.AuthorData;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Repository
public class AuthorRegistryRepository {
  private final Map<String, AuthorData> authors;

  public AuthorRegistryRepository() {
    authors = new HashMap<>();

    authors.put("title1", new AuthorData("firstName1", "secondName1"));
    authors.put("title2", new AuthorData("firstName2", "secondName2"));
    authors.put("title3", new AuthorData("firstName3", "secondName3"));
  }
  public AuthorRegistryRepository(Map<String, AuthorData> authors) {
    this.authors = authors;
  }

  public boolean isBookOwner(String bookName, AuthorData authorData) {
    AuthorData author = authors.getOrDefault(bookName, null);

    System.out.println(author);
    if (author == null) {
      return false;
    }

    return Objects.equals(author.firstName(), authorData.firstName())
        && Objects.equals(author.lastName(), authorData.lastName());
  }
}
