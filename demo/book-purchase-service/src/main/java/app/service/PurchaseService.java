package app.service;

import app.exceptions.UserNotFoundException;
import app.user.User;
import app.user.UserRepository;
import app.outbox.Outbox;
import app.outbox.OutboxRepository;
import app.service.response.MessageResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;

@Service
public class PurchaseService {
  private final ObjectMapper objectMapper;
  private final OutboxRepository outboxRepository;
  private final UserRepository userRepository;

  @Autowired
  public PurchaseService(
      OutboxRepository outboxRepository,
      UserRepository userRepository,
      ObjectMapper objectMapper) {
    this.outboxRepository = outboxRepository;
    this.userRepository = userRepository;
    this.objectMapper = objectMapper;
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public void buyBook(Long bookId) throws JsonProcessingException {
    var bookCost = 31L;

    var user = userRepository.findAll().get(0);

    String messageToSend;

    if (user.getCount() < bookCost) {
      messageToSend =
          objectMapper.writeValueAsString(
              new MessageResponse(bookId, false, "Not enough money on the user"));

    } else {
      user.decreaseOnDelta(bookCost);
      userRepository.save(user);

      messageToSend =
          objectMapper.writeValueAsString(new MessageResponse(bookId, true, "Success"));
    }

    outboxRepository.save(new Outbox(messageToSend));
  }

  public void setAccountMoney(long count) {
    var user = userRepository.findAll().get(0);

    user.setCount(count);

    userRepository.save(user);
  }
}
