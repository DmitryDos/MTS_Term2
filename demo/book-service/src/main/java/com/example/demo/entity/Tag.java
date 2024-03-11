package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "tags")
public class Tag {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  protected long id;

  @Getter
  @Setter
  private String name;

  @ManyToMany(mappedBy = "tags")
  private Set<Book> books = new HashSet<>();
  protected Tag() {}
  public Tag(String name) {
    this.name = name;
  }

  public Tag(String name, Set<Book> books) {
    this.name = name;
    this.books = books;
  }
}
