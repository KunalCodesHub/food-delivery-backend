package com.fooddelivery.model;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {
 
 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;
 
 @ManyToOne(fetch = FetchType.LAZY)
 @JoinColumn(name = "order_id")
 private Order order;
 
 @ManyToOne(fetch = FetchType.LAZY)
 @JoinColumn(name = "menu_item_id")
 private MenuItem menuItem;
 
 private String itemName;
 
 private Integer quantity;
 
 private Double price;
 
 private Double totalPrice;
 
 private String specialInstructions;
}