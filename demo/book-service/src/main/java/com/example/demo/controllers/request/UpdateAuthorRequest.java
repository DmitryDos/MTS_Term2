package com.example.demo.controllers.request;

import com.example.demo.entity.Author;
import com.example.demo.entity.Book;
import com.example.demo.entity.Tag;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public class UpdateAuthorRequest {
  @NotNull
  public String firstName;
  @NotNull
  public String secondName;

  public UpdateAuthorRequest(String firstName, String secondName, Set<Book> books) {
    this.firstName = firstName;
    this.secondName = secondName;
  }
}
