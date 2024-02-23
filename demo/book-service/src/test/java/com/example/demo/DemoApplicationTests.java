package com.example.demo;

import com.example.demo.controllers.request.CreateBookRequest;
import com.example.demo.controllers.request.FindBookByTagRequest;
import com.example.demo.controllers.request.UpdateBookRequest;
import com.example.demo.entity.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DemoApplicationTests {
	@Autowired
	private TestRestTemplate rest;

	@Test
	void createAndUpdateBook() {
		rest.put("/api/book", new CreateBookRequest("Kristy Golden", "Warcraft", Collections.singleton("Tag1")));

		ResponseEntity<Book> getBookByIdResponse =
				rest.getForEntity("/api/book/0", Book.class);

		assertTrue(getBookByIdResponse.getStatusCode().is2xxSuccessful(), "Unexpected status code: " + getBookByIdResponse.getStatusCode());
		Book getBookByIdResponseBody = getBookByIdResponse.getBody();
    assert getBookByIdResponseBody != null;
    assertEquals("Kristy Golden", getBookByIdResponseBody.getAuthor());

		rest.put("/api/book/0", new UpdateBookRequest("Lev Tolstoy", "War and Peace", Collections.singleton("Tag3")));

		ResponseEntity<Book> getBookByTagResponse =
				rest.getForEntity("/api/tags/Tag3", Book.class);

		assertTrue(getBookByTagResponse.getStatusCode().is2xxSuccessful(), "Unexpected status code: " + getBookByTagResponse.getStatusCode());
		Book getBookByTagResponseBody = getBookByTagResponse.getBody();

    assert getBookByTagResponseBody != null;
		
		assertEquals("War and Peace", getBookByTagResponseBody.getTitle());
		rest.delete("/api/book/0");
	}
}

