package app.service;

import app.controllers.request.MessageRequest;
import app.controllers.response.BookStatusResponse;
import app.outbox.Outbox;
import app.outbox.OutboxRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

import static org.springframework.transaction.event.TransactionPhase.BEFORE_COMMIT;

@Service
public class EventListener {
  private final OutboxRepository outboxRepository;
  private final ObjectMapper objectMapper;

  public EventListener(OutboxRepository outboxRepository, ObjectMapper objectMapper) {
    this.outboxRepository = outboxRepository;
    this.objectMapper = objectMapper;
  }

  @TransactionalEventListener(phase = BEFORE_COMMIT)
  public void onUserRolesUpdated(BookStatusResponse event) throws JsonProcessingException {
    var outboxEntity =
        new Outbox(
            objectMapper.writeValueAsString(
                new MessageRequest(event.bookId(), event.messageId())));

    outboxRepository.save(outboxEntity);
  }
}
