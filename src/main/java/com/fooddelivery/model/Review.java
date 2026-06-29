package com.fooddelivery.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {
 
 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;
 
 @ManyToOne(fetch = FetchType.LAZY)
 @JoinColumn(name = "user_id")
 private User user;
 
 @ManyToOne(fetch = FetchType.LAZY)
 @JoinColumn(name = "restaurant_id")
 private Restaurant restaurant;
 
 @ManyToOne(fetch = FetchType.LAZY)
 @JoinColumn(name = "order_id")
 private Order order;
 
 private Integer rating;  // 1-5
 
 @Column(length = 1000)
 private String comment;
 
 private String reviewImage;
 
 // AI-generated sentiment analysis
 private String sentiment;  // POSITIVE, NEGATIVE, NEUTRAL
 
 @Column(updatable = false)
 private LocalDateTime createdAt;
 
 @PrePersist
 protected void onCreate() {
     createdAt = LocalDateTime.now();
 }
}
