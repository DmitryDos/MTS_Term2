package com.example.demo.controllers.response;

import com.example.demo.entity.Author;
import com.example.demo.entity.Tag;

import java.util.Set;

public record FindBookResponse(Long id, String title, Author author, Set<Tag> tags) {}