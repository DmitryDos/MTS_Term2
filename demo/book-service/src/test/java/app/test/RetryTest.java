package app.test;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

import app.controllers.request.IfAuthorIsOwnerRequest;
import app.controllers.response.IfAuthorIsOwnerResponse;
import app.service.AuthorService;
import app.service.BookService;
import app.sources.DatabaseSuite;
import app.sources.TestDataSourceConfiguration;
import io.github.resilience4j.springboot3.retry.autoconfigure.RetryAutoConfiguration;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ContextConfiguration(classes = TestDataSourceConfiguration.class)
public class RetryTest extends DatabaseSuite {
  @MockBean private RestTemplate restTemplate;
  @Autowired private BookService bookService;
  @Autowired private AuthorService authorService;

  @Test
  void retryOneTimeRequest() {
    var author = authorService.create("firstName1", "secondName1");

    var uuid = UUID.randomUUID().toString();

    HttpHeaders headers = new HttpHeaders();
    headers.add("X-REQUEST-ID", uuid);

    var template = when(restTemplate.exchange(
            "/api/author-registry-service",
            HttpMethod.POST,
            new HttpEntity<>(
                new IfAuthorIsOwnerRequest(
                    author.getFirstName(), author.getLastName(), "title1"),
                headers),
            IfAuthorIsOwnerResponse.class))
            .thenThrow(new RestClientException("Unexpected error"));

    assertThrows(
        RestClientException.class,
        () -> bookService.create("title1", author.getId(), uuid));

    HttpEntity<IfAuthorIsOwnerRequest> entity = new HttpEntity<>(
        new IfAuthorIsOwnerRequest(author.getFirstName(), author.getLastName(), "title1"),
        headers);

    verify(restTemplate, times(1))
        .exchange("/api/author-registry-service", HttpMethod.POST, entity, IfAuthorIsOwnerResponse.class);
  }
}