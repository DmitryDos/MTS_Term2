package com.example.demo.controllers.request;

import jakarta.validation.constraints.NotNull;

import java.util.Set;

public class UpdateBookRequest {
  @NotNull
  public String author;
  @NotNull
  public String title;
  @NotNull
  public Set<String> tags;
}
