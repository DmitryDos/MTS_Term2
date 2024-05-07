package app;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import java.util.UUID;

import app.controllers.request.IfAuthorIsOwnerRequest;
import app.controllers.response.IfAuthorIsOwnerResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class AuthorRegistryControllerTest {
	@Autowired private TestRestTemplate rest;

	@Test
	void authorIsOwnerCheck() {
		HttpHeaders headers = new HttpHeaders();

		headers.add("X-REQUEST-ID", UUID.randomUUID().toString());

		ResponseEntity<IfAuthorIsOwnerResponse> checkAuthorRegistryResponse =
				rest.exchange(
						"/api/author-registry-service",
						HttpMethod.POST,
						new HttpEntity<>(
								new IfAuthorIsOwnerRequest("firstName1", "secondName1", "title1"), headers),
						IfAuthorIsOwnerResponse.class);

		assertNotNull(checkAuthorRegistryResponse.getBody());
    assertTrue(checkAuthorRegistryResponse.getBody().isOwner());
	}

	@Test
	void authorIsNotOwnerCheck() {
		HttpHeaders headers = new HttpHeaders();

		headers.add("X-REQUEST-ID", UUID.randomUUID().toString());

		ResponseEntity<IfAuthorIsOwnerResponse> checkAuthorRegistryResponse =
				rest.exchange(
						"/api/author-registry-service",
						HttpMethod.POST,
						new HttpEntity<>(new IfAuthorIsOwnerRequest("firstName1", "secondName1", "title#"), headers),
						IfAuthorIsOwnerResponse.class);

		assertNotNull(checkAuthorRegistryResponse.getBody());
    assertFalse(checkAuthorRegistryResponse.getBody().isOwner());
	}
}
