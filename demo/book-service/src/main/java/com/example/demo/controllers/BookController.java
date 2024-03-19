package com.example.demo.controllers;

import com.example.demo.controllers.response.CreateBookResponse;
import com.example.demo.controllers.response.FindBookResponse;
import com.example.demo.repositories.exceptions.BookNotFoundException;
import com.example.demo.controllers.request.CreateBookRequest;
import com.example.demo.controllers.request.UpdateBookRequest;
import com.example.demo.entity.Book;
import com.example.demo.service.BookService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;

@RestController
@ControllerAdvice
@RequestMapping("/api/books")
public class BookController {
  private final BookService bookService;

  @Autowired
  public BookController(BookService bookService) {
    this.bookService = bookService;
  }

  @GetMapping("/{id}")
  public FindBookResponse getBookById(
      @NotNull
      @PathVariable Long id
  ) throws BookNotFoundException {
    Book findBook = this.bookService.findById(id);
    return new FindBookResponse(findBook.getId(), findBook.getTitle(), findBook.getAuthor(), findBook.getTags());
  }

  @PostMapping()
  public CreateBookResponse createBook(
      @NotNull
      @RequestBody
      @Valid CreateBookRequest request
  ) {
    Book createdBook = this.bookService.create(request.title(), request.authorId());
    return new CreateBookResponse(createdBook.getId(), createdBook.getTitle(), createdBook.getAuthorId());
  }

  @PutMapping("/{id}")
  public void updateBook(
      @NotNull
      @PathVariable Long id,
      @NotNull
      @RequestBody
      @Valid UpdateBookRequest request
  ) throws BookNotFoundException {
    bookService.updateBookTitle(id, request.title());
    bookService.updateBookAuthor(id, request.authorId());
  }

  @DeleteMapping("/{id}")
  public void deleteBook(
      @NotNull
      @PathVariable Long id
  ) throws BookNotFoundException {
    bookService.delete(id);
  }

  @PutMapping("/{id}/tags/{tagId}")
  public void addTag(
      @NotNull
      @PathVariable Long id,
      @NotNull
      @PathVariable Long tagId
  ) throws BookNotFoundException {
    bookService.addTag(id, tagId);
  }

  @DeleteMapping("/{id}/tags/{tagId}")
  public void removeTag(
      @NotNull
      @PathVariable Long id,
      @NotNull
      @PathVariable Long tagId
  ) throws BookNotFoundException {
    bookService.removeTag(id, tagId);
  }
}
