package com.example.demo.controllers;

import com.example.demo.bookRepository.BookRepository;
import com.example.demo.bookRepository.exceptions.BookNotFoundException;
import com.example.demo.controllers.request.CreateBookRequest;
import com.example.demo.controllers.request.FindBookByTagRequest;
import com.example.demo.controllers.request.GetBookRequest;
import com.example.demo.controllers.request.UpdateBookRequest;
import com.example.demo.controllers.response.GetBookResponse;
import com.example.demo.entity.Book;
import com.example.demo.entity.BookWithoutId;
import com.example.demo.service.BookService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Validated
public class BookController {
  private final BookService bookService;

  public BookController(BookService bookService) {
    this.bookService = bookService;
  }

  @GetMapping("/book/{id}")
  public ResponseEntity<?> getBookById(
      @NotNull
      @PathVariable Long id
  ) {
    try {
      Book book = bookService.getById(id);
      return ResponseEntity.ok(book);
    } catch (BookNotFoundException e) {
      return ResponseEntity.status(422).body(e);
    }
  }

  @PutMapping("/book")
  public ResponseEntity<?> createBook(
      @NotNull
      @RequestBody
      @Valid CreateBookRequest request
  ) {
    try {

      BookWithoutId book = new BookWithoutId(request.author, request.title, request.tags);
      long id = bookService.create(book);
      return ResponseEntity.ok(id);
    } catch (Exception e) {
      return ResponseEntity.status(422).body(e);
    }
  }

  @GetMapping("/book")
  public ResponseEntity<?> getBookByTag(
      @RequestBody FindBookByTagRequest request
  ) {
    try {
      Book book = bookService.getByTag(request.tag);
      return ResponseEntity.ok(book);
    } catch (BookNotFoundException e) {
      return ResponseEntity.status(422).body(e);
    }
  }

  @PutMapping("/book/{id}")
  public void updateBook(
      @NotNull
      @PathVariable Long id,
      @NotNull
      @RequestBody
      @Valid UpdateBookRequest request
  ) throws BookNotFoundException {
    Book book = new Book(request.author, request.title, request.tags, id);
    bookService.update(book);
  }
}
