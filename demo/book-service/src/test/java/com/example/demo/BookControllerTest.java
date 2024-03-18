package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.example.demo.controllers.request.CreateAuthorRequest;
import com.example.demo.controllers.request.CreateBookRequest;
import com.example.demo.controllers.request.CreateTagRequest;
import com.example.demo.controllers.response.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ContextConfiguration(classes = TestDataSourceConfiguration.class)
class BookControllerTest {
  @Autowired private TestRestTemplate rest;

  @Test
  void bookE2ETest() {
    ResponseEntity<CreateTagResponse> createTagResponseEntity1 = rest.postForEntity(
        "/api/tags",
        new CreateTagRequest("tag1"),
        CreateTagResponse.class);

    assertNotNull(createTagResponseEntity1.getBody());
    assertEquals(createTagResponseEntity1.getBody().name(), "tag1");

    ResponseEntity<CreateTagResponse> createTagResponseEntity2 =
        rest.postForEntity(
            "/api/tags", new CreateTagRequest("tag2"), CreateTagResponse.class);

    assertNotNull(createTagResponseEntity2.getBody());

    ResponseEntity<CreateAuthorResponse> createAuthorResponseEntity =
        rest.postForEntity(
            "/api/authors",
            new CreateAuthorRequest("FirstName1", "SecondName1"),
            CreateAuthorResponse.class);

    assertNotNull(createAuthorResponseEntity.getBody());
    assertEquals(createAuthorResponseEntity.getBody().firstName(), "FirstName1");
    assertEquals(createAuthorResponseEntity.getBody().lastName(), "SecondName1");

    ResponseEntity<CreateBookResponse> createBookResponseEntity =
        rest.postForEntity(
            "/api/books",
            new CreateBookRequest( createAuthorResponseEntity.getBody().id(), "Book1"),
            CreateBookResponse.class);


    assertNotNull(createBookResponseEntity.getBody());
    assertEquals(createBookResponseEntity.getBody().title(), "Book1");

    rest.put(
        "/api/books/"
            + createBookResponseEntity.getBody().id()
            + "/tags/"
            + createTagResponseEntity1.getBody().id(),
        null
    );

    rest.put(
            "/api/books/"
                + createBookResponseEntity.getBody().id()
                + "/tags/"
                + createTagResponseEntity2.getBody().id(),
            null
    );

    ResponseEntity<FindBookResponse> getBookResponseEntity =
        rest.getForEntity(
            "/api/books/" + createBookResponseEntity.getBody().id(), FindBookResponse.class);

    assertNotNull(getBookResponseEntity.getBody());
    assertEquals(getBookResponseEntity.getBody().author().getFirstName(), "FirstName1");
    assertEquals(getBookResponseEntity.getBody().author().getLastName(), "SecondName1");
    assertEquals(getBookResponseEntity.getBody().tags().size(), 2);
  }
}