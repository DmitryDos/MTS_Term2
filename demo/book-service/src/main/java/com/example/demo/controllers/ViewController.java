package com.example.demo.controllers;

import com.example.demo.bookRepository.BookRepository;
import com.example.demo.entity.Book;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/api")
public class ViewController {
  private final BookRepository bookRepository;

  public ViewController(BookRepository bookRepository) {
    this.bookRepository = bookRepository;
  }

  @GetMapping("/book")
  public String viewBooks(Model model) {
    List<Book> books = bookRepository.getAll();
    model.addAttribute("books", books);
    return "books";
  }
}
