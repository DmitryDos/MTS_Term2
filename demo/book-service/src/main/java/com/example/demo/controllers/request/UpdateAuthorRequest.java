package com.example.demo.controllers.request;

import com.example.demo.entity.Author;
import com.example.demo.entity.Book;
import com.example.demo.entity.Tag;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record UpdateAuthorRequest (String firstName, String secondName) {}
