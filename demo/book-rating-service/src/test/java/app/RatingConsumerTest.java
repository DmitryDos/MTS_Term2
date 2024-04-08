package app;

import app.consumer.BookRatingConsumer;
import app.kafka.RatingBookMessageRequest;
import app.producer.BookRatingProducer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerRecord;
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
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.times;

@SpringBootTest(
    classes = {BookRatingConsumer.class},
    properties = {
        "topic-to-consume-message=some-test-topic",
        "spring.kafka.consumer.group-id=some-consumer-group",
        "spring.kafka.consumer.auto-offset-reset=earliest"
    })
@Import({
    KafkaAutoConfiguration.class,
    RatingConsumerTest.ObjectMapperTestConfig.class,
})
@Testcontainers
@DirtiesContext()
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

  @MockBean
  private BookRatingProducer messageProcessor;
  @Autowired private BookRatingConsumer bookRatingConsumer;

  @Autowired private ObjectMapper objectMapper;

  @Test
  void shouldSendMessageToKafkaSuccessfully() throws JsonProcessingException {
    kafkaTemplate.send(
        new ProducerRecord<>(
            "some-test-topic", objectMapper.writeValueAsString(new RatingBookMessageRequest(56L))));

    await()
        .atMost(Duration.ofSeconds(5))
        .pollDelay(Duration.ofSeconds(1))
        .untilAsserted(() -> Mockito.verify(messageProcessor, times(1)).stubBookRating(56L));
  }
}
