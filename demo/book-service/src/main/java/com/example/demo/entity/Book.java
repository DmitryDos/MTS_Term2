package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Setter
@Getter
@Table(name = "books")
public class Book {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   protected long id;

   @ManyToOne(fetch = FetchType.EAGER)
   @JoinColumn(name = "author_id", insertable = false, updatable = false)
   @NotNull(message = "Book author have to be filled")
   private Author author;

   @NotNull(message = "Book title have to be filled")
   private String title;

   @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
   @JoinTable(
       name = "tag_book",
       joinColumns = @JoinColumn(name = "book_id"),
       inverseJoinColumns = @JoinColumn(name = "tag_id")
   )
   private Set<Tag> tags = new HashSet<>();

   protected Book() {}

   public Book(Author author, String title) {
      this.author = author;
      this.title = title;
   }
}
