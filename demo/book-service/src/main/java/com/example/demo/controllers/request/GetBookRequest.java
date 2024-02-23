package com.example.demo.controllers.request;

import jakarta.validation.constraints.NotNull;

public class GetBookRequest {
  @NotNull
  public long id;
}
