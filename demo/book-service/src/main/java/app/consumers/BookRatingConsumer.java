package app.consumers;

import app.kafka.RatingBookMessageResponse;
import app.repositories.exceptions.BookNotFoundException;
import app.service.BookService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class BookRatingConsumer {
  private final ObjectMapper objectMapper;
  @Autowired
  private final BookService bookService;

  public BookRatingConsumer(ObjectMapper objectMapper, BookService bookService) {
    this.objectMapper = objectMapper;
    this.bookService = bookService;
  }

  @KafkaListener(topics = {"${topic-to-consume-message}"})
  public void consumeCalculatedBookRating(String message)
      throws JsonProcessingException, BookNotFoundException {
    RatingBookMessageResponse parsedMessage =
        objectMapper.readValue(message, RatingBookMessageResponse.class);

    bookService.updateRating(parsedMessage.bookId(), parsedMessage.rating());
  }
}
