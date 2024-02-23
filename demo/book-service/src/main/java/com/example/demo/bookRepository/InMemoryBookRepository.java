package com.example.demo.bookRepository;

import com.example.demo.bookRepository.exceptions.BookNotFoundException;
import com.example.demo.entity.Book;
import com.example.demo.entity.BookWithoutId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
@Component("in-mem")
public class InMemoryBookRepository implements BookRepository {

  private Map<Long, Book> books = new ConcurrentHashMap<>();

  private final AtomicLong nextId = new AtomicLong(0);

  @Override
  public long generateId() {
    return nextId.getAndIncrement();
  }
  @Override
  public List<Book> getAll() {
    return new ArrayList<>(books.values());
  }

  @Override
  public Book getById(long id) throws BookNotFoundException {
    if (!books.containsKey(id)) {
      throw new BookNotFoundException("Book have not been found by Id");
    }
    return books.get(id);
  }
  @Override
  public Book getByTag(String tag) throws BookNotFoundException {
    for (Long id : books.keySet()) {
      if (books.get(id).getTags().contains(tag)) {
        return books.get(id);
      }
    }
    throw new BookNotFoundException("Book have not been found by Id");
  }
  @Override
  public long create(BookWithoutId bookWithoutId) {
    long id = generateId();
    Book book = new Book(bookWithoutId, id);
    books.put(id, book);
    return id;
  }

  @Override
  public long update(Book book) throws BookNotFoundException {
    long id = book.getId();
    if (!books.containsKey(id)) {
      throw new BookNotFoundException("Book have not been found by Id");
    }
    books.put(id, book);
    return id;
  }

  @Override
  public void delete(long id) throws BookNotFoundException {
    if (!books.containsKey(id)) {
      throw new BookNotFoundException("Book have not been found by Id");
    }
    books.remove(id);
  }
}
