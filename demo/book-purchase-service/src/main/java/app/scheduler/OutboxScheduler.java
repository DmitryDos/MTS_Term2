package app.scheduler;

import app.outbox.Outbox;
import app.outbox.OutboxRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class OutboxScheduler {
  private final KafkaTemplate<String, String> kafkaTemplate;
  private final String topic;
  private final OutboxRepository outboxRepository;
  public OutboxScheduler(
      KafkaTemplate<String, String> kafkaTemplate,
      OutboxRepository outboxRepository,
      @Value("${" +
          "topic-to-send-buy-message}") String topic) {
    this.kafkaTemplate = kafkaTemplate;
    this.topic = topic;
    this.outboxRepository = outboxRepository;
  }

  @Transactional
  @Scheduled(fixedDelay = 10000)
  public void processOutbox() {
    List<Outbox> result = outboxRepository.findAll();
    for (Outbox outbox : result) {
      send(outbox);
    }
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void send(Outbox outbox) {
    CompletableFuture<SendResult<String, String>> sendResult =
        kafkaTemplate.send(topic, outbox.getData());
    outbox.setSend(true);

    outboxRepository.save(outbox);
  }
}
