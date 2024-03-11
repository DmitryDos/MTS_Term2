package com.example.demo.controllers;

import com.example.demo.controllers.request.CreateAuthorRequest;
import com.example.demo.controllers.request.CreateTagRequest;
import com.example.demo.controllers.request.UpdateAuthorRequest;
import com.example.demo.controllers.request.UpdateTagRequest;
import com.example.demo.entity.Author;
import com.example.demo.entity.Tag;
import com.example.demo.repositories.exceptions.AuthorNotFoundException;
import com.example.demo.repositories.exceptions.TagNotFoundException;
import com.example.demo.service.AuthorService;
import com.example.demo.service.TagService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@ControllerAdvice
@RequestMapping("/api/tags")
@Validated
public class TagController {
  private final TagService tagService;
  public TagController(TagService tagService) {
    this.tagService = tagService;
  }

  @GetMapping("/tag/{id}")
  public ResponseEntity<?> getTagById(
      @NotNull
      @PathVariable Long id
  ) throws TagNotFoundException {
    try {
      Tag tag = tagService.findById(id);
      return ResponseEntity.ok(tag);
    } catch (Exception e) {
      return ResponseEntity.status(422).body(e);
    }
  }

  @PutMapping("/tag")
  public ResponseEntity<?> createTag(
      @NotNull
      @RequestBody
      @Valid CreateTagRequest request
  ) {
    try {
      Tag tag = tagService.create(new Tag(request.name, request.books));
      return ResponseEntity.ok(tag);
    } catch (Exception e) {
      return ResponseEntity.status(422).body(e);
    }
  }

  @PutMapping("/tag/{id}")
  public void updateTag(
      @NotNull
      @PathVariable Long id,
      @NotNull
      @RequestBody
      @Valid UpdateTagRequest request
  ) throws TagNotFoundException {
    tagService.updateTag(id, request.name);
  }

  @DeleteMapping("/tag/{id}")
  public void deleteTag(
      @NotNull
      @PathVariable Long id
  ) throws TagNotFoundException {
    tagService.delete(id);
  }
}
