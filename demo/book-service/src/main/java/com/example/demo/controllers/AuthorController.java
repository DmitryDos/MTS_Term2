package com.example.demo.controllers;

import com.example.demo.controllers.request.CreateAuthorRequest;
import com.example.demo.controllers.request.CreateBookRequest;
import com.example.demo.controllers.request.UpdateAuthorRequest;
import com.example.demo.controllers.request.UpdateBookRequest;
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
import org.springframework.boot.autoconfigure.cassandra.CassandraProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.awt.desktop.PreferencesEvent;
import java.util.Optional;

@RestController
@ControllerAdvice
@RequestMapping("/api/authors")
@Validated
public class AuthorController {
  private final AuthorService authorService;
  public AuthorController(AuthorService authorService) {
    this.authorService = authorService;
  }

  @GetMapping("/author/{id}")
  public ResponseEntity<?> getAuthorById(
      @NotNull
      @PathVariable Long id
  ) throws AuthorNotFoundException {
    try {
      Author author = authorService.findById(id);
      return ResponseEntity.ok(author);
    } catch (Exception e) {
      return ResponseEntity.status(422).body(e);
    }
  }

  @PutMapping("/author")
  public ResponseEntity<?> createAuthor(
      @NotNull
      @RequestBody
      @Valid CreateAuthorRequest request
  ) {
    try {
      Author author = authorService.create(new Author(request.firstName, request.secondName, request.books));
      return ResponseEntity.ok(author);
    } catch (Exception e) {
      return ResponseEntity.status(422).body(e);
    }
  }

  @PutMapping("/author/{id}")
  public void updateAuthor(
      @NotNull
      @PathVariable Long id,
      @NotNull
      @RequestBody
      @Valid UpdateAuthorRequest request
  ) throws AuthorNotFoundException {
    authorService.updateAuthorInfo(id, request.firstName, request.secondName);
  }

  @DeleteMapping("/author/{id}")
  public void deleteAuthor(
      @NotNull
      @PathVariable Long id
  ) throws AuthorNotFoundException {
    authorService.delete(id);
  }
}
