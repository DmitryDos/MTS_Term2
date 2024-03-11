package com.example.demo.service;

import com.example.demo.entity.Tag;
import com.example.demo.repositories.TagRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
    return tagRepository.findById(id).orElseThrow();
  }

  @Transactional
  public Tag create(Tag tag) {
    return tagRepository.save(tag);
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
