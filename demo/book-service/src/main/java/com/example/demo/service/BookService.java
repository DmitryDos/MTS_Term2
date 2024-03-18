package com.example.demo.service;

import com.example.demo.entity.Author;
import com.example.demo.entity.Tag;
import com.example.demo.repositories.BookRepository;
import com.example.demo.repositories.TagRepository;
import com.example.demo.repositories.exceptions.BookNotFoundException;
import com.example.demo.entity.Book;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {
  private final BookRepository bookRepository;
  private final TagRepository tagRepository;
  @Autowired
  public BookService(BookRepository bookRepository, TagRepository tagRepository) {
    this.bookRepository = bookRepository;
    this.tagRepository = tagRepository;
  }

  public List<Book> getAll() {
    return bookRepository.findAll();
  }

  public Book findById(long id) throws BookNotFoundException {
    try {
      return this.bookRepository.findById(id).orElseThrow();
    } catch (Exception e) {
      throw (new BookNotFoundException("Book not found"));
    }
  }

  public Book create(String title, Long authorId) {
    return this.bookRepository.save(new Book(title, authorId));
  }

  @Transactional
  public void updateBookTitle(Long bookId, String newTitle) {
    Book book = bookRepository.findById(bookId).orElseThrow();
    book.setTitle(newTitle);
    bookRepository.save(book);
  }
  @Transactional
  public void updateBookAuthor(Long bookId, Long authorId) {
    Book book = bookRepository.findById(bookId).orElseThrow();
    book.setAuthorId(authorId);
    bookRepository.save(book);
  }

  @Transactional
  public void delete(Long bookId) {
    bookRepository.deleteById(bookId);
  }

  @Transactional
  public void addTag(Long bookId, Long tagId) {
    Book book = bookRepository.findById(bookId).orElseThrow();
    Tag tag = tagRepository.findById(tagId).orElseThrow();

    book.addTag(tag);
    bookRepository.save(book);
  }

  @Transactional
  public void removeTag(Long bookId, Long tagId) {
    Book book = bookRepository.findById(bookId).orElseThrow();
    Tag tag = tagRepository.findById(tagId).orElseThrow();

    book.removeTag(tag);
    bookRepository.save(book);
  }
}
