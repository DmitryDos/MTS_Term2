package app.entities;

import app.controllers.response.BookStatusResponse;
import org.springframework.data.domain.AbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "books")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Book extends AbstractAggregateRoot<Book> {
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

   @Setter
   @Column(name = "rating", nullable = false, precision = 3, scale = 2)
   private BigDecimal rating = BigDecimal.ZERO;

   @Enumerated(EnumType.STRING)
   @Setter
   @Column(name = "status", nullable = false)
   private PaymentStatus status = PaymentStatus.NO_PAYMENT;

   public Book(String title, Long authorId, PaymentStatus paymentStatus) {}

   public void setPaymentStatusPending(UUID messageId) {
      this.setStatus(PaymentStatus.PAYMENT_PENDING);
      registerEvent(new BookStatusResponse(this.id, this.status, messageId.toString()));
   }

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
