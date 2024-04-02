package app.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "tags")
@Getter
@Setter
public class Tag {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  protected Tag() {}
  public Tag(String name) {
    this.name = name;
  }

  public Tag(Long id, String name) {
    this.id = id;
    this.name = name;
  }
}
