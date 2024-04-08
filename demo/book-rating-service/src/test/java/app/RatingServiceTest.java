package app;

import app.service.RatingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.testcontainers.junit.jupiter.Testcontainers;


import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
public class RatingServiceTest {
  private RatingService ratingService = new RatingService();

  @Test
  public void getRandomRating() {
    int r = (int) ratingService.getRating(1L);
    assertEquals(r, 3);
  }
}
