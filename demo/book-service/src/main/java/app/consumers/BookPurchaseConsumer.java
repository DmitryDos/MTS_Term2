package app.consumers;

import app.controllers.response.MessageResponse;
import app.entities.PaymentStatus;
import app.repositories.exceptions.BookNotFoundException;
import app.service.BookService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class BookPurchaseConsumer {
  private final ObjectMapper objectMapper;

  private final BookService bookService;

  public BookPurchaseConsumer(ObjectMapper objectMapper, BookService bookService) {
    this.objectMapper = objectMapper;
    this.bookService = bookService;
  }

  @KafkaListener(topics = {"${topic-to-consume-buy-message}"})
  public void consumeBookPurchaseResponse(String message)
      throws JsonProcessingException, BookNotFoundException {
    MessageResponse parsedMessage =
        objectMapper.readValue(message, MessageResponse.class);

    if (parsedMessage.success()) {
      bookService.updateBookPurchaseStatus(parsedMessage.bookId(), PaymentStatus.PAYMENT_SUCCEED);
    } else {
      bookService.updateBookPurchaseStatus(parsedMessage.bookId(), PaymentStatus.PAYMENT_PENDING);
    }
  }
}
