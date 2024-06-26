package app.controllers;

import app.entities.Book;
import app.service.AuthorService;
import app.service.BookService;
import app.service.TagService;
import app.entities.Author;
import app.entities.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/api")
public class ViewController {
  private final BookService bookService;
  private final AuthorService authorService;
  private final TagService tagService;

  @Autowired
  public ViewController(BookService bookService, AuthorService authorService, TagService tagService) {
    this.bookService = bookService;
    this.authorService = authorService;
    this.tagService = tagService;
  }

  @GetMapping("/book")
  public String viewBooks(Model model) {
    List<Book> books = bookService.getAll();
    model.addAttribute("books", books);
    return "books";
  }

  @GetMapping("/tag")
  public String viewTags(Model model) {
    List<Tag> tags = tagService.getAll();
    model.addAttribute("tags", tags);
    return "tags";
  }

  @GetMapping("/author")
  public String viewAuthors(Model model) {
    List<Author> authors = authorService.getAll();
    model.addAttribute("authors", authors);
    return "authors";
  }
}
