package app.consumer;

import app.service.PurchaseService;
import app.service.request.MessageRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
public class PurchaseConsumer {
  private final ObjectMapper objectMapper;

  private final PurchaseService purchaseService;

  private final Set<String> proceededMessages;

  public PurchaseConsumer(ObjectMapper objectMapper, PurchaseService purchaseService) {
    this.objectMapper = objectMapper;
    this.purchaseService = purchaseService;
    this.proceededMessages = new HashSet<>();
  }

  @KafkaListener(topics = {"${topic-to-consume-buy-message}"})
  @Transactional(propagation = Propagation.REQUIRED)
  public void consumeBookPurchaseResponse(String message) throws JsonProcessingException {
    MessageRequest parsedMessage =
        objectMapper.readValue(message, MessageRequest.class);

    if (proceededMessages.contains(parsedMessage.messageId())) return;

    proceededMessages.add(parsedMessage.messageId());

    purchaseService.buyBook(parsedMessage.bookId());
  }
}
