package app.test.rating;

import app.consumers.BookRatingConsumer;
import app.kafka.RatingBookMessageResponse;
import app.service.BookService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.times;

@SpringBootTest(
    classes = {BookRatingConsumer.class, BookService.class},
    properties = {
        "topic-to-consume-message=some-test-topic-rating",
        "spring.kafka.consumer.group-id=some-consumer-group-rating",
        "spring.kafka.consumer.auto-offset-reset=earliest"
    })
@Import({KafkaAutoConfiguration.class, RatingConsumerTest.ObjectMapperTestConfig.class})
@Testcontainers
public class RatingConsumerTest {
  @TestConfiguration
  static class ObjectMapperTestConfig {
    @Bean
    public ObjectMapper objectMapper() {
      return new ObjectMapper();
    }
  }

  @Container
  @ServiceConnection
  public static final KafkaContainer KAFKA =
      new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.0"));

  @Autowired
  private KafkaTemplate<String, String> kafkaTemplate;
  @Autowired private ObjectMapper objectMapper;
  @MockBean
  private BookService bookService;
  @Autowired private BookRatingConsumer bookRatingConsumer;

  @Test
  void successMessage() throws JsonProcessingException {

    kafkaTemplate.send(
        "some-test-topic-rating",
        objectMapper.writeValueAsString(new RatingBookMessageResponse(1L, 2.5D)));

    await()
        .atMost(Duration.ofSeconds(50))
        .pollDelay(Duration.ofSeconds(1))
        .untilAsserted(() -> Mockito.verify(bookService, times(1)).updateRating(1L, 2.5));
  }
}