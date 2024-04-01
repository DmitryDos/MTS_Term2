package app.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import app.controllers.request.CreateAuthorRequest;
import app.controllers.request.CreateTagRequest;
import app.controllers.response.CreateAuthorResponse;
import app.controllers.response.CreateBookResponse;
import app.controllers.response.CreateTagResponse;
import app.controllers.response.FindBookResponse;
import app.controllers.request.CreateBookRequest;
import app.sources.TestDataSourceConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MockServerContainer;
import org.mockserver.client.MockServerClient;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.mockserver.model.HttpResponse;
import static org.mockserver.model.HttpRequest.request;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ContextConfiguration(classes = TestDataSourceConfiguration.class)
class BookControllerTest {
  @Autowired private TestRestTemplate rest;

  @Container
  public static final MockServerContainer mockServer =
      new MockServerContainer(DockerImageName.parse("mockserver/mockserver:5.13.2"));
  @DynamicPropertySource
  static void setProperties(DynamicPropertyRegistry registry) {
    registry.add("author-registry-service.service.base.url", mockServer::getEndpoint);
  }

  @Test
  void bookE2ETest() {
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

    ResponseEntity<CreateTagResponse> createTagResponse1 = rest.postForEntity(
        "/api/tags",
        new CreateTagRequest("tag1"),
        CreateTagResponse.class);

    assertNotNull(createTagResponse1.getBody());
    assertEquals(createTagResponse1.getBody().name(), "tag1");

    ResponseEntity<CreateTagResponse> createTagResponse2 =
        rest.postForEntity(
            "/api/tags", new CreateTagRequest("tag2"), CreateTagResponse.class);

    assertNotNull(createTagResponse2.getBody());

    ResponseEntity<CreateAuthorResponse> createAuthorResponse =
        rest.postForEntity(
            "/api/authors",
            new CreateAuthorRequest("firstName1", "secondName1"),
            CreateAuthorResponse.class);

    assertNotNull(createAuthorResponse.getBody());
    assertEquals(createAuthorResponse.getBody().firstName(), "firstName1");
    assertEquals(createAuthorResponse.getBody().lastName(), "secondName1");

    System.out.println(createAuthorResponse.getBody().id());
    ResponseEntity<CreateBookResponse> createBookResponse =
        rest.postForEntity(
            "/api/books",
            new CreateBookRequest(createAuthorResponse.getBody().id(), "title1"),
            CreateBookResponse.class);


    System.out.println(createBookResponse);

    assertNotNull(createBookResponse.getBody());
    assertEquals(createBookResponse.getBody().title(), "title1");

    rest.put(
        "/api/books/"
            + createBookResponse.getBody().id()
            + "/tags/"
            + createTagResponse1.getBody().id(),
        null
    );

    rest.put(
            "/api/books/"
                + createBookResponse.getBody().id()
                + "/tags/"
                + createTagResponse2.getBody().id(),
            null
    );

    ResponseEntity<FindBookResponse> getBookResponse =
        rest.getForEntity(
            "/api/books/" + createBookResponse.getBody().id(), FindBookResponse.class);

    assertNotNull(getBookResponse.getBody());
    assertEquals(getBookResponse.getBody().author().getFirstName(), "firstName1");
    assertEquals(getBookResponse.getBody().author().getLastName(), "secondName1");
    assertEquals(getBookResponse.getBody().tags().size(), 2);
  }
}