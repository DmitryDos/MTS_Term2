package com.example.demo.controllers.request;

import com.example.demo.entity.Author;
import com.example.demo.entity.Book;
import com.example.demo.entity.Tag;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public class CreateAuthorRequest {
  @NotNull
  public String firstName;
  @NotNull
  public String secondName;
  @NotNull
  public Set<Book> books;

  public CreateAuthorRequest(String firstName, String secondName, Set<Book> books) {
    this.firstName = firstName;
    this.secondName = secondName;
    this.books = books;
  }
}
