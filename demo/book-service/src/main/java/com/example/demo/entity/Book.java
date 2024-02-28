package com.example.demo.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Getter
@Setter
public class Book extends BookWithoutId{
   private long id;
   private String author;
   private String title;
   private Set<String> tags;

   public Book() {}

   public Book(String author, String title, Set<String> tags, long id) {
      this.author = author;
      this.title = title;
      this.tags = tags;
      this.id = id;
   }

   public Book(BookWithoutId book, long id) {
      this.author = book.getAuthor();
      this.title = book.getTitle();
      this.tags = book.getTags();
      this.id = id;
   }
}
