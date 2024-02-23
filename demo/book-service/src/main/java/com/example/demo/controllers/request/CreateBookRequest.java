package com.example.demo.controllers.request;

import jakarta.validation.constraints.NotNull;

import java.util.Set;

public class CreateBookRequest {
  @NotNull
  public String author;
  @NotNull
  public String title;
  @NotNull
  public Set<String> tags;

  public CreateBookRequest(String author, String title, Set<String> tags) {
    this.author = author;
    this.title = title;
    this.tags = tags;
  }
}
