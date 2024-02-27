package com.example.demo.bookRepository;

import com.example.demo.bookRepository.exceptions.BookNotFoundException;
import com.example.demo.entity.Book;
import com.example.demo.entity.BookWithoutId;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface BookRepository {
  List<Book> getAll();
  Book getById(long id) throws BookNotFoundException;
  Book getByTag(String tag) throws BookNotFoundException;
  long create(BookWithoutId bookWithoutId);
  long update(Book book) throws BookNotFoundException;
  void delete(long id) throws BookNotFoundException;
}
