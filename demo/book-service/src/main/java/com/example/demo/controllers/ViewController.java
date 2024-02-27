package com.example.demo.controllers;

import com.example.demo.entity.Book;
import com.example.demo.service.BookService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/api")
public class ViewController {
  private final BookService bookService;

  public ViewController(BookService bookService) {
    this.bookService = bookService;
  }

  @GetMapping("/book")
  public String viewBooks(Model model) {
    List<Book> books = bookService.getAll();
    model.addAttribute("books", books);
    return "books";
  }
}
