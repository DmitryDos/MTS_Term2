package com.example.demo.service;

import com.example.demo.bookRepository.BookRepository;
import com.example.demo.bookRepository.exceptions.BookNotFoundException;
import com.example.demo.entity.Book;
import com.example.demo.entity.BookWithoutId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {
  private final BookRepository bookRepository;

  @Autowired
  public BookService(@Qualifier("in-mem") BookRepository bookRepository) {
    this.bookRepository = bookRepository;
  }

  public List<Book> getAll() {
    return bookRepository.getAll();
  }

  public Book getById(long id) throws BookNotFoundException {
    return bookRepository.getById(id);
  }

  public long create(BookWithoutId bookWithoutId) {
    return bookRepository.create(bookWithoutId);
  }

  public long update(Book book) throws BookNotFoundException {
    return bookRepository.update(book);
  }

  public void delete(long id) throws BookNotFoundException {
    bookRepository.delete(id);
  }

  public Book getByTag(String tag) throws BookNotFoundException {
    return bookRepository.getByTag(tag);
  }
}
