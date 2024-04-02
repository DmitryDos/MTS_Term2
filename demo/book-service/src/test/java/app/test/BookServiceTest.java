package app.test;

import static org.junit.jupiter.api.Assertions.*;

import app.entities.Author;
import app.entities.Book;
import app.repositories.exceptions.AuthorIsNotOwnerException;
import app.repositories.exceptions.AuthorNotFoundException;
import app.repositories.exceptions.BookNotFoundException;
import app.service.AuthorService;
import app.service.BookService;
import app.sources.DatabaseSuite;
import app.sources.TestConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.HttpResponse;
import org.springframework.http.*;

import java.util.UUID;

import static org.mockserver.model.HttpRequest.request;

@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({TestConfig.class, BookService.class, AuthorService.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class BookServiceTest extends DatabaseSuite {
  @Autowired
  private BookService bookService;

  @Autowired
  private AuthorService authorService;

  @Container
  public static final MockServerContainer mockServer =
      new MockServerContainer(DockerImageName.parse("mockserver/mockserver:5.13.2"));

  @BeforeAll
  static void setUp() {
    mockServer.start();

    MockServerClient client = new MockServerClient(mockServer.getHost(), mockServer.getServerPort());
    client
        .when(
            request()
                .withMethod(String.valueOf(HttpMethod.POST))
                .withHeader("X-REQUEST-ID")
                .withPath("/api/author-registry-service"))
        .respond(
            new HttpResponse()
                .withBody("{\"isOwner\": \"true\"}")
                .withHeader("Content-Type", "application/json"));
  }

  @DynamicPropertySource
  static void setProperties(DynamicPropertyRegistry registry) {
    registry.add("author-registry-service.service.base.url", mockServer::getEndpoint);
  }

  @Test
  public void createBook() throws AuthorNotFoundException, AuthorIsNotOwnerException {
    Author author = authorService.create("firstName1", "secondName1");
    Book book = bookService.create("title1", author.getId(), UUID.randomUUID().toString());

    assertEquals(book.getTitle(), "title1");
  }

  @Test
  public void updateBook() throws AuthorNotFoundException, AuthorIsNotOwnerException, BookNotFoundException {
    Author author = authorService.create("firstName2", "secondName2");
    Book book = bookService.create("title2", author.getId(), UUID.randomUUID().toString());

    bookService.updateBookTitle(book.getId(), "titleUpdated");

    Book updatedBook = bookService.findById(book.getId());
    assertEquals(updatedBook.getTitle(), "titleUpdated");
  }

  @Test
  public void getBookById() throws AuthorNotFoundException, AuthorIsNotOwnerException, BookNotFoundException {
    Author author = authorService.create("firstName3", "secondName3");
    Book book = bookService.create("title3", author.getId(), UUID.randomUUID().toString());

    Book recievedBook = bookService.findById(book.getId());
    assertEquals(recievedBook.getId(), book.getId());
    assertEquals(recievedBook.getTitle(), "title3");
    assertEquals(recievedBook.getAuthorId(), book.getAuthorId());
  }
}