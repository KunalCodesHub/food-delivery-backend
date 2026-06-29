package com.fooddelivery.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cart_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem {
 
 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;
 
 @ManyToOne(fetch = FetchType.LAZY)
 @JoinColumn(name = "cart_id")
 private Cart cart;
 
 @ManyToOne(fetch = FetchType.LAZY)
 @JoinColumn(name = "menu_item_id")
 private MenuItem menuItem;
 
 private Integer quantity = 1;
 
 private Double price;
 
 private String specialInstructions;
}
