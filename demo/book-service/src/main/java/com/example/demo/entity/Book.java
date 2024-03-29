package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "books")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Book {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private long id;

   @ManyToOne(fetch = FetchType.EAGER)
   @JoinColumn(name = "author_id", insertable = false, updatable = false)
   private Author author;

   @Setter
   @Column(name = "author_id")
   private Long authorId;

   private String title;

   @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
   @JoinTable(
       name = "tag_book",
       joinColumns = @JoinColumn(name = "book_id"),
       inverseJoinColumns = @JoinColumn(name = "tag_id")
   )
   private Set<Tag> tags = new HashSet<>();

   public Book(String title, Long authorId) {
      this.authorId = authorId;
      this.title = title;
   }
   public void addTag(Tag tag) {
      tags.add(tag);
   }
   public void removeTag(Tag tag) {
      tags.remove(tag);
   }
}
