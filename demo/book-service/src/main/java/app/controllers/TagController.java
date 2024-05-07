package app.controllers;

import app.controllers.request.CreateTagRequest;
import app.controllers.request.UpdateTagRequest;
import app.controllers.response.CreateTagResponse;
import app.controllers.response.FindTagResponse;
import app.entities.Tag;
import app.repositories.exceptions.TagNotFoundException;
import app.service.TagService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
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
