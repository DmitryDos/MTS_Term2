package com.example.demo;

import static org.junit.jupiter.api.Assertions.*;

import com.example.demo.entity.Author;
import com.example.demo.entity.Book;
import com.example.demo.repositories.exceptions.AuthorNotFoundException;
import com.example.demo.repositories.exceptions.BookNotFoundException;
import com.example.demo.service.AuthorService;
import com.example.demo.service.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({BookService.class, AuthorService.class})
class BookServiceTest extends DatabaseSuite {
  @Autowired private BookService bookService;

  @Autowired private AuthorService authorService;

  @Test
  void CreateBookTest() throws AuthorNotFoundException {
    var author = authorService.create(new Author("FirstName1", "SurName1"));
    var book = bookService.create(new Book(author, "bookName1"));

    assertEquals(book.getTitle(), "bookName1");
  }

  @Test
  void getBookByIdTest() throws BookNotFoundException, AuthorNotFoundException {
    var author = authorService.create(new Author("FirstName2", "SurName2"));

    var book = bookService.create(new Book(author, "bookName2"));

    Book retrievedBook = bookService.findById(book.getId());

    assertEquals(retrievedBook.getId(), book.getId());
    assertEquals(retrievedBook.getTitle(), "bookName2");
    assertEquals(retrievedBook.getAuthor().getFirstName(), "FirstName2");
    assertEquals(retrievedBook.getAuthor().getLastName(), "SurName2");
  }

  @Test
  void updateBookTest() throws BookNotFoundException, AuthorNotFoundException {
    var author = authorService.create(new Author("FirstName3", "SurName3"));

    var book = bookService.create(new Book(author, "bookName3"));

    bookService.updateBookTitle(book.getId(), "New book Title");

    var updatedBook = bookService.findById(book.getId());

    assertEquals(updatedBook.getTitle(), "New book Title");
  }

  @Test
  void deleteBookTest() throws BookNotFoundException, AuthorNotFoundException {
    var author = authorService.create(new Author("FirstName4", "SurName4"));

    var book = bookService.create(new Book(author, "bookName4"));

    bookService.delete(book.getId());

    assertThrows(BookNotFoundException.class, () -> bookService.findById(book.getId()));
  }
}