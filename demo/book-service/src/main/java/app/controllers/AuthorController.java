package app.controllers;

import app.controllers.request.CreateAuthorRequest;
import app.service.AuthorService;
import app.controllers.request.UpdateAuthorRequest;
import app.controllers.response.CreateAuthorResponse;
import app.controllers.response.FindAuthorResponse;
import app.entities.Author;
import app.repositories.exceptions.AuthorNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/authors")
public class AuthorController {
  private final AuthorService authorService;

  @Autowired
  public AuthorController(AuthorService authorService) {
    this.authorService = authorService;
  }

  @GetMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public FindAuthorResponse getAuthorById(
      @NotNull
      @PathVariable Long id
  ) throws AuthorNotFoundException {
    Author findAuthor = authorService.findById(id);
    return new FindAuthorResponse(findAuthor.getId(), findAuthor.getFirstName(), findAuthor.getLastName());
  }

  @PostMapping()
  @ResponseStatus(HttpStatus.OK)
  public CreateAuthorResponse createAuthor(
      @NotNull
      @RequestBody
      @Valid CreateAuthorRequest request
  ) {
    Author createdAuthor = authorService.create(request.firstName(), request.secondName());
    return new CreateAuthorResponse(createdAuthor.getId(), createdAuthor.getFirstName(), createdAuthor.getLastName());
  }

  @PutMapping("/author/{id}")
  @ResponseStatus(HttpStatus.OK)
  public void updateAuthor(
      @NotNull
      @PathVariable Long id,
      @NotNull
      @RequestBody
      @Valid UpdateAuthorRequest request
  ) throws AuthorNotFoundException {
    authorService.updateAuthorInfo(id, request.firstName(), request.secondName());
  }

  @DeleteMapping("/author/{id}")
  @ResponseStatus(HttpStatus.OK)
  public void deleteAuthor(
      @NotNull
      @PathVariable Long id
  ) throws AuthorNotFoundException {
    authorService.delete(id);
  }
}
