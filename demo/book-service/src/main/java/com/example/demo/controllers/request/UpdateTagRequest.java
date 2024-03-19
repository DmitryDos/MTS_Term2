package com.example.demo.controllers.request;

import com.example.demo.entity.Book;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record UpdateTagRequest (String name) {}
