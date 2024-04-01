package app.test;

import app.controllers.request.CreateAuthorRequest;
import app.controllers.request.CreateBookRequest;
import app.controllers.response.CreateAuthorResponse;
import app.controllers.response.CreateBookResponse;
import app.controllers.response.FindBookResponse;
import app.entities.Author;
import app.entities.Book;
import app.entities.Tag;
import app.repositories.exceptions.AuthorIsNotOwnerException;
import app.repositories.exceptions.AuthorNotFoundException;
import app.service.AuthorService;
import app.service.BookService;
import app.service.TagService;
import app.sources.DatabaseSuite;
import app.sources.TestConfig;
import app.sources.TestDataSourceConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.HttpResponse;
import org.springframework.http.*;

import java.util.UUID;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockserver.model.HttpRequest.request;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ContextConfiguration(classes = TestDataSourceConfiguration.class)
public class TimeoutTest extends DatabaseSuite {
  @Autowired private AuthorService authorService;

  @Autowired private BookService bookService;

  @Container
  public static final MockServerContainer mockServer =
      new MockServerContainer(DockerImageName.parse("mockserver/mockserver:5.13.2"));

  @DynamicPropertySource
  static void setProperties(DynamicPropertyRegistry registry) {
    registry.add("author-registry-service.service.base.url", mockServer::getEndpoint);
  }

  @BeforeEach
  void setUp() {
    mockServer.start();

    MockServerClient client = new MockServerClient(mockServer.getHost(), mockServer.getServerPort());
    client
        .when(
            request()
                .withMethod(String.valueOf(HttpMethod.POST))
                .withHeader("X-REQUEST-ID")
                .withPath("/api/author-registry-service"))
        .respond(
            req -> {
              Thread.sleep(100000);
              return new HttpResponse()
                  .withBody("{\"isOwner\": \"true\"}")
                  .withHeader("Content-Type", "application/json");
            });
  }
  @Test
  void timeOutExceptionRequest() throws AuthorNotFoundException, AuthorIsNotOwnerException {
    Author author = authorService.create("firstName1", "secondName1");

    assertThrows(RestClientException.class,
        () -> bookService.create("title1", author.getId(), UUID.randomUUID().toString()));
  }
}
