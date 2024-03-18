package com.example.demo.controllers;

import com.example.demo.controllers.request.CreateAuthorRequest;
import com.example.demo.controllers.request.CreateBookRequest;
import com.example.demo.controllers.request.UpdateAuthorRequest;
import com.example.demo.controllers.request.UpdateBookRequest;
import com.example.demo.controllers.response.CreateAuthorResponse;
import com.example.demo.controllers.response.FindAuthorResponse;
import com.example.demo.entity.Author;
import com.example.demo.entity.Book;
import com.example.demo.entity.Tag;
import com.example.demo.repositories.AuthorRepository;
import com.example.demo.repositories.exceptions.AuthorNotFoundException;
import com.example.demo.repositories.exceptions.BookNotFoundException;
import com.example.demo.service.AuthorService;
import com.example.demo.service.BookService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cassandra.CassandraProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
  public FindAuthorResponse getAuthorById(
      @NotNull
      @PathVariable Long id
  ) throws AuthorNotFoundException {
    Author findAuthor = authorService.findById(id);
    return new FindAuthorResponse(findAuthor.getId(), findAuthor.getFirstName(), findAuthor.getLastName());
  }

  @PostMapping()
  public CreateAuthorResponse createAuthor(
      @NotNull
      @RequestBody
      @Valid CreateAuthorRequest request
  ) {
    Author createdAuthor = authorService.create(request.firstName(), request.secondName());
    return new CreateAuthorResponse(createdAuthor.getId(), createdAuthor.getFirstName(), createdAuthor.getLastName());
  }

  @PutMapping("/author/{id}")
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
  public void deleteAuthor(
      @NotNull
      @PathVariable Long id
  ) throws AuthorNotFoundException {
    authorService.delete(id);
  }
}
