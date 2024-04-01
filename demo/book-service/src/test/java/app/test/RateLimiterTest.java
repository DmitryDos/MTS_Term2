package app.test;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;

import app.controllers.request.IfAuthorIsOwnerRequest;
import app.controllers.response.IfAuthorIsOwnerResponse;
import app.repositories.exceptions.BookNotFoundException;
import app.service.AuthorService;
import app.service.BookService;
import app.sources.DatabaseSuite;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.springboot3.ratelimiter.autoconfigure.RateLimiterAutoConfiguration;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.junit.jupiter.Testcontainers;

@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({BookService.class, AuthorService.class, RateLimiterAutoConfiguration.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@EnableAspectJAutoProxy
public class RateLimiterTest extends DatabaseSuite {
  @MockBean private RestTemplate restTemplate;
  @Autowired private BookService bookService;
  @Autowired private AuthorService authorService;

  @Test
  void requestNotPermittedRequest() {
    var author = authorService.create("firstName1", "secondName1");

    var uuid = UUID.randomUUID().toString();

    HttpHeaders headers = new HttpHeaders();
    headers.add("X-REQUEST-ID", uuid);

    when(restTemplate.exchange(
        "/api/author-registry-service",
        HttpMethod.POST,
        new HttpEntity<>(
            new IfAuthorIsOwnerRequest(
                author.getFirstName(), author.getLastName(), "title1"),
            headers),
        IfAuthorIsOwnerResponse.class))
        .thenAnswer(
            (Answer<ResponseEntity<IfAuthorIsOwnerResponse>>)
                invocation -> {
                  Thread.sleep(10000);
                  return new ResponseEntity<>(new IfAuthorIsOwnerResponse(true), HttpStatus.OK);
                });

    assertDoesNotThrow(() -> bookService.create("title1", author.getId(), uuid));

    assertThrows(
        RequestNotPermitted.class,
        () -> bookService.create("title1", author.getId(), uuid));
  }
}