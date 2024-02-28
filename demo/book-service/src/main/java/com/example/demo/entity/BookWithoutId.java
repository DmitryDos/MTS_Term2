package com.example.demo.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Getter
@Setter
public class BookWithoutId {
  private String author;
  private String title;
  private Set<String> tags;

  public BookWithoutId() {}

  public BookWithoutId(String author, String title, Set<String> tags) {
    this.author = author;
    this.title = title;
    this.tags = tags;
  }
}
