package com.example.demo.service;

import com.example.demo.entity.Author;
import com.example.demo.repositories.AuthorRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthorService {
  private final AuthorRepository authorRepository;

  @Autowired
  public AuthorService(AuthorRepository authorRepository) {
    this.authorRepository = authorRepository;
  }

  public List<Author> getAll() {
    return authorRepository.findAll();
  }

  public Author findById(long id) {
    return authorRepository.findById(id).orElseThrow();
  }

  public Author create(Author author) {
    return authorRepository.save(author);
  }

  @Transactional
  public void updateAuthorInfo(Long id, String firstName, String lastName) {
    Author author = authorRepository.findById(id).orElseThrow();
    author.setFirstName(firstName);
    author.setLastName(lastName);
    authorRepository.save(author);
  }

  @Transactional
  public void delete(Long id) {
    Author author = authorRepository.findById(id).orElseThrow();
    authorRepository.delete(author);
  }
}
