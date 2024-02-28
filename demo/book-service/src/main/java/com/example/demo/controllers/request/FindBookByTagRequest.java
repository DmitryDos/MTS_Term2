package com.example.demo.controllers.request;

import jakarta.validation.constraints.NotNull;

public class FindBookByTagRequest {
  @NotNull
  public String tag;

  public FindBookByTagRequest(String tag) {
    this.tag = tag;
  }
}
