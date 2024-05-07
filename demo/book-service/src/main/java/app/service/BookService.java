package app.service;

import app.controllers.request.IfAuthorIsOwnerRequest;
import app.controllers.response.BuyBookResponse;
import app.controllers.response.CreateBookResponse;
import app.controllers.response.IfAuthorIsOwnerResponse;
import app.controllers.response.MessageResponse;
import app.entities.Author;
import app.entities.PaymentStatus;
import app.entities.Tag;
import app.repositories.AuthorRepository;
import app.repositories.BookRepository;
import app.repositories.TagRepository;

import app.repositories.exceptions.AuthorIsNotOwnerException;
import app.repositories.exceptions.AuthorNotFoundException;
import app.repositories.exceptions.BookNotFoundException;
import app.entities.Book;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class BookService {
  private final BookRepository bookRepository;
  private final TagRepository tagRepository;
  private final AuthorRepository authorRepository;
  public final RestTemplate restTemplate;

  @Autowired
  public BookService(BookRepository bookRepository, TagRepository tagRepository, AuthorRepository authorRepository, RestTemplate restTemplate) {
    this.bookRepository = bookRepository;
    this.tagRepository = tagRepository;
    this.authorRepository = authorRepository;
    this.restTemplate = restTemplate;
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

  @RateLimiter(name = "createBook", fallbackMethod = "fallbackRateLimiter")
  @CircuitBreaker(name = "createBook", fallbackMethod = "fallbackCircuitBreaker")
  @Retry(name = "createBook", fallbackMethod = "fallbackRetry")
  @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = {Throwable.class})
  public Book create(String title, Long authorId, String requestId) throws AuthorNotFoundException, AuthorIsNotOwnerException {
    try {
      Author author = authorRepository.findById(authorId).orElseThrow();

      System.out.println(author);

      HttpHeaders headers = new HttpHeaders();

      headers.add("X-REQUEST-ID", requestId);

      HttpEntity<IfAuthorIsOwnerRequest> entity = new HttpEntity<>(
          new IfAuthorIsOwnerRequest(author.getFirstName(), author.getLastName(), title),
          headers);

      var authorRegistry =
          restTemplate.exchange(
              "/api/author-registry-service",
              HttpMethod.POST,
              entity,
              IfAuthorIsOwnerResponse.class);

      if (authorRegistry.getBody() == null) {
        throw new RestClientException("Unsuccessful request to author registry service");
      }
      System.out.println(authorRegistry.getBody());
      if (!authorRegistry.getBody().isOwner()) {
        throw new AuthorIsNotOwnerException(authorId);
      }

      return this.bookRepository.save(new Book(title, authorId));
    } catch (RestClientException e) {
      throw new RestClientException("Unsuccessful request to author registry service");
    }
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

  @Transactional
  public void updateRating(Long bookId, Double rating) {
    var book = bookRepository.findById(bookId).orElseThrow();

    book.setRating(BigDecimal.valueOf(rating));

    bookRepository.save(book);
  }

  public BuyBookResponse buyById(Long bookId) throws BookNotFoundException {
    var book = bookRepository.findById(bookId).orElse(null);

    if (book == null) {
      throw new BookNotFoundException(bookId.toString() + " book not found");
    }

    switch (book.getStatus()) {
      case PAYMENT_PENDING -> {
        return new BuyBookResponse("Payment is already in progress");
      }
      case PAYMENT_SUCCEED -> {
        return new BuyBookResponse("Payment has been already finished");
      }
    }

    book.setPaymentStatusPending(UUID.randomUUID());
    bookRepository.save(book);

    return new BuyBookResponse("Waiting for payment...");
  }

  public void updateBookPurchaseStatus(long bookId, PaymentStatus status) throws BookNotFoundException {
    var book = findById(bookId);

    book.setStatus(status);
  }
}
