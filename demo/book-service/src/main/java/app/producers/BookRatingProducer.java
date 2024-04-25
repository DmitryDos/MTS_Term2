package app.producers;

import app.kafka.RatingBookMessageRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
public class BookRatingProducer {
  private final KafkaTemplate<String, String> kafkaTemplate;
  private final ObjectMapper objectMapper;
  private final String topic;

  public BookRatingProducer(
      KafkaTemplate<String, String> kafkaTemplate,
      ObjectMapper objectMapper,
      @Value("${topic-to-send-message}") String topic) {
    this.kafkaTemplate = kafkaTemplate;
    this.objectMapper = objectMapper;
    this.topic = topic;
  }

  public void getRating(@PathVariable @NotNull Long bookId)
      throws JsonProcessingException {
    String message = objectMapper.writeValueAsString(new RatingBookMessageRequest(bookId));
    var key = "book-" + bookId;
    CompletableFuture<SendResult<String, String>> sendResult =
        kafkaTemplate.send(topic, key, message);

    try {
      sendResult.get(2, TimeUnit.SECONDS);
    } catch (InterruptedException | ExecutionException | TimeoutException e) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException("Unexpected thread interruption", e);
    }
//    catch (ExecutionException e) {
//      throw new BookRatingException("Couldn't send message to Kafka");
//    } catch (TimeoutException e) {
//      throw new BookRatingException("Couldn't send message to Kafka due to timeout");
//    }
  }
}
