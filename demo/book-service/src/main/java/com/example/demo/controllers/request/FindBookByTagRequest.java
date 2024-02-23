package com.example.demo.controllers.request;

import jakarta.validation.constraints.NotNull;

public class FindBookByTagRequest {
  @NotNull
  public String tag;
}
