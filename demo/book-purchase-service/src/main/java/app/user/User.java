package app.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import static jakarta.persistence.GenerationType.IDENTITY;

@Table(name = "account")
@Entity
@Getter
@Setter
public class User {
  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long count = 0L;

  public void decreaseOnDelta(Long delta) {
    this.count -= delta;
  }
}
