package app.service;

import app.repositories.TagRepository;
import app.entities.Tag;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagService {
  private final TagRepository tagRepository;

  @Autowired
  public TagService(TagRepository tagRepository) {
    this.tagRepository = tagRepository;
  }

  @Transactional
  public List<Tag> getAll() {
    return tagRepository.findAll();
  }

  @Transactional
  public Tag findById(long id) {
    return this.tagRepository.findById(id).orElseThrow();
  }

  @Transactional
  public Tag create(String name) {
    return this.tagRepository.save(new Tag(name));
  }

  @Transactional
  public void updateTag(Long id, String name) {
    Tag tag = tagRepository.findById(id).orElseThrow();
    tag.setName(name);
    tagRepository.save(tag);
  }

  @Transactional
  public void delete(Long id) {
    Tag tag = tagRepository.findById(id).orElseThrow();
    tagRepository.delete(tag);
  }
}
