package app;

import app.consumer.PurchaseConsumer;
import app.exceptions.UserNotFoundException;
import app.scheduler.OutboxScheduler;
import app.scheduler.SchedulerConfig;
import app.service.PurchaseService;
import app.service.request.MessageRequest;
import app.service.response.MessageResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.apache.kafka.common.serialization.StringDeserializer;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.Properties;

@SpringBootTest(
    properties = {
        "topic-to-send-message=test-response-topic",
        "topic-to-consume-message=test-request-topic",
        "spring.kafka.consumer.auto-offset-reset=earliest"
    })
@Import({
    PurchaseConsumer.class,
    KafkaAutoConfiguration.class,
    SchedulerConfig.class,
    OutboxScheduler.class
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@Testcontainers
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class SuccessText {
  @Container
  @ServiceConnection
  public static final KafkaContainer KAFKA =
      new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.0"));

  @Autowired private PurchaseService bookService;
  @Autowired private PurchaseConsumer purchaseConsumer;
  @Autowired
  private ObjectMapper objectMapper;
  @Autowired private KafkaTemplate<String, String> kafkaTemplate;

  @Test
  void shouldSendSuccessMessageToKafkaE2ETest()
      throws JsonProcessingException, InterruptedException, UserNotFoundException {
    var uuid = UUID.randomUUID().toString();

    bookService.setAccountMoney(1000L);

    kafkaTemplate.send(
        "test-request-topic", objectMapper.writeValueAsString(new MessageRequest(1L, uuid)));

    KafkaTestConsumer consumer = new KafkaTestConsumer(KAFKA.getBootstrapServers(), "some-group");
    consumer.subscribe(List.of("test-response-topic"));

    Thread.sleep(10_000);

    ConsumerRecords<String, String> records = consumer.poll();
    records
        .iterator()
        .forEachRemaining(
            record -> {
              MessageResponse message;
              try {
                message = objectMapper.readValue(record.value(), MessageResponse.class);
              } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
              }

              System.out.println(message);
              assertTrue(message.success());
              assertEquals(message.message(), "Success");
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
