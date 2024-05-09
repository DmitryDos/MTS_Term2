package app.test.purchase;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.times;

import app.consumers.BookPurchaseConsumer;
import app.controllers.response.MessageResponse;
import app.entities.PaymentStatus;
import app.service.BookService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
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


@SpringBootTest(
    classes = {BookPurchaseConsumer.class, BookService.class},
    properties = {
        "topic-to-send-message=test-send-message",
        "topic-to-consume-buy-message=test-consume-message",
        "spring.kafka.consumer.auto-offset-reset=earliest",
        "spring.flyway.baseline-on-migrate=true",
        "spring.flyway.enabled=false"
    })
@Import({KafkaAutoConfiguration.class, PurchaseConsumerTest.ObjectMapperTestConfig.class, BookService.class})
@Testcontainers
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class PurchaseConsumerTest {
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
  @Autowired private BookPurchaseConsumer bookPurchaseConsumer;

  @Test
  void successMessage() throws JsonProcessingException {

    kafkaTemplate.send(
        "test-consume-message",
        objectMapper.writeValueAsString(new MessageResponse(1L, true, "Success message")));

    await()
        .atMost(Duration.ofSeconds(10))
        .pollDelay(Duration.ofSeconds(1))
        .untilAsserted(
            () ->
                Mockito.verify(bookService, times(1))
                    .updateBookPurchaseStatus(1L, PaymentStatus.PAYMENT_SUCCEED));
  }

  @Test
  void failMessage() throws JsonProcessingException {

    kafkaTemplate.send(
        "test-consume-message",
        objectMapper.writeValueAsString(new MessageResponse(1L, false, "Fail message")));

    await()
        .atMost(Duration.ofSeconds(10))
        .pollDelay(Duration.ofSeconds(1))
        .untilAsserted(
            () ->
                Mockito.verify(bookService, times(1))
                    .updateBookPurchaseStatus(1L, PaymentStatus.PAYMENT_PENDING));
  }
}
