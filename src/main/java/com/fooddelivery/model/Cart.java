package com.fooddelivery.model;


import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cart {
 
 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;
 
 @OneToOne
 @JoinColumn(name = "user_id")
 private User user;
 
 @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
 private List<CartItem> items = new ArrayList<>();
 
 private Double totalAmount = 0.0;
 
 // Calculating total amount
 public void calculateTotal() {
     this.totalAmount = items.stream()
         .mapToDouble(item -> item.getPrice() * item.getQuantity())
         .sum();
 }
}
