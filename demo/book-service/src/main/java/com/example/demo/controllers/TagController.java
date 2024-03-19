package com.example.demo.controllers;

import com.example.demo.controllers.request.CreateAuthorRequest;
import com.example.demo.controllers.request.CreateTagRequest;
import com.example.demo.controllers.request.UpdateAuthorRequest;
import com.example.demo.controllers.request.UpdateTagRequest;
import com.example.demo.controllers.response.CreateTagResponse;
import com.example.demo.controllers.response.FindTagResponse;
import com.example.demo.entity.Author;
import com.example.demo.entity.Tag;
import com.example.demo.repositories.exceptions.AuthorNotFoundException;
import com.example.demo.repositories.exceptions.TagNotFoundException;
import com.example.demo.service.AuthorService;
import com.example.demo.service.TagService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tags")
public class TagController {
  private final TagService tagService;

  @Autowired
  public TagController(TagService tagService) {
    this.tagService = tagService;
  }

  @PostMapping()
  public CreateTagResponse createTag(
      @RequestBody
      CreateTagRequest request
  ) {
    Tag createdTag = tagService.create(request.name());
    return new CreateTagResponse(createdTag.getId().toString(), createdTag.getName());
  }

  @GetMapping("/{id}")
  public FindTagResponse getTagById(
      @NotNull
      @PathVariable Long id
  ) throws TagNotFoundException {
    Tag findTag = tagService.findById(id);
    return new FindTagResponse(findTag.getId().toString(), findTag.getName());
  }

  @PutMapping("/{id}")
  public void updateTag(
      @NotNull
      @PathVariable Long id,
      @NotNull
      @RequestBody
      @Valid UpdateTagRequest request
  ) throws TagNotFoundException {
    tagService.updateTag(id, request.name());
  }

  @DeleteMapping("/{id}")
  public void deleteTag(
      @NotNull
      @PathVariable Long id
  ) throws TagNotFoundException {
    tagService.delete(id);
  }
}
