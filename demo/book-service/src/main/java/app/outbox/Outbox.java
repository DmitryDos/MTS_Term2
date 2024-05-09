package app.outbox;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import static jakarta.persistence.GenerationType.IDENTITY;

@Table(name = "outbox")
@Entity
@Getter
@Setter
public class Outbox {
  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = IDENTITY)
  private Long id;

  @Column(name = "data")
  private String data;

  @Column(name = "send")
  private boolean send;

  public Outbox(String data) {
    this.data = data;
  }

  public Outbox() {
  }
}
