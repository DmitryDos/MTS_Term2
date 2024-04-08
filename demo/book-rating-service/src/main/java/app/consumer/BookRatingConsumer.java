package app.consumer;

import app.kafka.RatingBookMessageRequest;
import app.producer.BookRatingProducer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class BookRatingConsumer {
  private final ObjectMapper objectMapper;

  @Autowired
  private final BookRatingProducer bookRatingProducer;
  public BookRatingConsumer(ObjectMapper objectMapper, BookRatingProducer bookRatingProducer) {
    this.objectMapper = objectMapper;
    this.bookRatingProducer = bookRatingProducer;
  }
  @KafkaListener(topics = {"${topic-to-consume-message}"})
  public void processBookRatingCalculationRequest(String message)
      throws JsonProcessingException, InterruptedException {
    RatingBookMessageRequest parsedMessage =
        objectMapper.readValue(message, RatingBookMessageRequest.class);

    bookRatingProducer.stubBookRating(parsedMessage.bookId());
  }
}
