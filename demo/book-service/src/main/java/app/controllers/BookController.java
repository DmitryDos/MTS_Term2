package app.controllers;

import app.controllers.response.CreateBookResponse;
import app.controllers.response.FindBookResponse;
import app.entities.Book;
import app.repositories.exceptions.AuthorIsNotOwnerException;
import app.repositories.exceptions.AuthorNotFoundException;
import app.repositories.exceptions.BookNotFoundException;
import app.service.BookService;
import app.controllers.request.CreateBookRequest;
import app.controllers.request.UpdateBookRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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
      @RequestBody
      CreateBookRequest request
  ) throws AuthorNotFoundException, AuthorIsNotOwnerException {
    System.out.println(request);
    Book createdBook = this.bookService.create(request.title(), request.authorId(), UUID.randomUUID().toString());
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
