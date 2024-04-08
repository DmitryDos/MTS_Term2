package app;

import app.kafka.RatingBookMessageResponse;
import app.producer.BookRatingProducer;
import app.service.RatingService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
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
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
    classes = {BookRatingProducer.class, RatingService.class},
    properties = {"topic-to-send-message=some-test-topic"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Import({
    KafkaAutoConfiguration.class,
    RatingProducerTest.ObjectMapperTestConfig.class,
})
@Testcontainers
public class RatingProducerTest {
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

  @MockBean
  private RatingService ratingService;
  @Autowired
  private BookRatingProducer bookRatingProducer;
  @Autowired private ObjectMapper objectMapper;

  @Test
  void shouldSendMessageToKafkaSuccessfully() {
    Mockito.when(ratingService.getRating(1L)).thenReturn(2.2);

    assertDoesNotThrow(() -> bookRatingProducer.stubBookRating(1L));

    KafkaTestConsumer consumer =
        new KafkaTestConsumer(KAFKA.getBootstrapServers(), "some-group-id");
    consumer.subscribe(List.of("some-test-topic"));

    ConsumerRecords<String, String> records = consumer.poll();
    assertEquals(1, records.count());
    records
        .iterator()
        .forEachRemaining(
            record -> {
              RatingBookMessageResponse message;
              try {
                message = objectMapper.readValue(record.value(), RatingBookMessageResponse.class);
              } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
              }
              assertEquals(new RatingBookMessageResponse(1L, 2.2), message);
            });
  }

  private static class KafkaTestConsumer {
    private final KafkaConsumer<String, String> consumer;

    public KafkaTestConsumer(String bootstrapServers, String groupId) {
      Properties props = new Properties();

      props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
      props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
      props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
      props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
      props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

      this.consumer = new KafkaConsumer<>(props);
    }

    public void subscribe(List<String> topics) {
      consumer.subscribe(topics);
    }

    public ConsumerRecords<String, String> poll() {
      return consumer.poll(Duration.ofSeconds(5));
    }
  }
}
