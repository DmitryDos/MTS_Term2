package com.example.demo.controllers.request;

import com.example.demo.entity.Book;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public class UpdateTagRequest {
  @NotNull
  public String name;

  public UpdateTagRequest(String name) {
    this.name = name;
  }
}
