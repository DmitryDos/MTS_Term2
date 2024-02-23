package com.example.demo.controllers.request;

import jakarta.validation.constraints.NotNull;

public class GetBookRequest {
  @NotNull
  public long id;

  public GetBookRequest(long id) {
    this.id = id;
  }
}
