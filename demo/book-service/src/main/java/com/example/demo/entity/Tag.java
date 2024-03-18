package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;


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
