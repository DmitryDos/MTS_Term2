package com.example.demo.controllers.request;

import com.example.demo.entity.Author;
import com.example.demo.entity.Tag;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public class UpdateBookRequest {
  @NotNull
  public Author author;
  @NotNull
  public String title;
  @NotNull
  public Set<Tag> tags;

  public UpdateBookRequest(Author author, String title, Set<Tag> tags) {
    this.author = author;
    this.title = title;
    this.tags = tags;
  }
}
