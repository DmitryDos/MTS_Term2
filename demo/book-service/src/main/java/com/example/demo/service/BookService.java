package com.example.demo.service;

import com.example.demo.entity.Author;
import com.example.demo.repositories.BookRepository;
import com.example.demo.repositories.exceptions.BookNotFoundException;
import com.example.demo.entity.Book;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {
  private final BookRepository bookRepository;

  @Autowired
  public BookService(BookRepository bookRepository) {
    this.bookRepository = bookRepository;
  }

  public List<Book> getAll() {
    return bookRepository.findAll();
  }

  public Book findById(long id) throws BookNotFoundException {
    return bookRepository.findById(id).orElseThrow();
  }

  public Book create(Book book) {
    return bookRepository.save(book);
  }

  @Transactional
  public void updateBookTitle(Long bookId, String newTitle) {
    Book book = bookRepository.findById(bookId).orElseThrow();
    book.setTitle(newTitle);
    bookRepository.save(book);
  }
  @Transactional
  public void updateBookAuthor(Long bookId, Author newAuthor) {
    Book book = bookRepository.findById(bookId).orElseThrow();
    book.setAuthor(newAuthor);
    bookRepository.save(book);
  }

  @Transactional
  public void delete(Long bookId) {
    Book book = bookRepository.findById(bookId).orElseThrow();
    bookRepository.delete(book);
  }
}
