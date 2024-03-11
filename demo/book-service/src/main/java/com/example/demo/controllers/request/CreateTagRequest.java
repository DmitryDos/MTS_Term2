package com.example.demo.controllers.request;

import com.example.demo.entity.Book;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public class CreateTagRequest {
  @NotNull
  public String name;
  @NotNull
  public Set<Book> books;

  public CreateTagRequest(String name, Set<Book> books) {
    this.name = name;
    this.books = books;
  }
}
