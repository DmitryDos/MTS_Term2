package app.controllers;

import app.controllers.request.IfAuthorIsOwnerRequest;
import app.controllers.response.IfAuthorIsOwnerResponse;
import app.entities.AuthorData;
import app.repositories.AuthorRegistryRepository;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api")
public class AuthorRegistryController {
  private final AtomicLong idGenerator = new AtomicLong(0);
  private final AuthorRegistryRepository authorRegistryRepository;

  private final Map<String, Boolean> requestIdMap = new ConcurrentHashMap<String, Boolean>();

  @Autowired
  public AuthorRegistryController(AuthorRegistryRepository authorRegistryRepository) {
    this.authorRegistryRepository = authorRegistryRepository;
  }

  @Operation(summary = "Check author registry")
  @PostMapping("/author-registry-service")
  @ResponseStatus(HttpStatus.OK)
  public IfAuthorIsOwnerResponse getAuthorRegistry(
      @NotNull @RequestHeader("X-REQUEST-ID") String requestId,
      @NotNull @RequestBody IfAuthorIsOwnerRequest ifAuthorIsOwnerRequest) {
    boolean isAuthor =
        requestIdMap.computeIfAbsent(
            requestId,
            k ->
                authorRegistryRepository.isBookOwner(
                    ifAuthorIsOwnerRequest.bookName(),
                    new AuthorData(
                        ifAuthorIsOwnerRequest.firstName(),
                        ifAuthorIsOwnerRequest.lastName())));

    return new IfAuthorIsOwnerResponse(isAuthor);
  }
}