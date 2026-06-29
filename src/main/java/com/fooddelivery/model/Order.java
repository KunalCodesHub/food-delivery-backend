package com.fooddelivery.model;


import com.fooddelivery.enums.OrderStatus;
import com.fooddelivery.enums.PaymentMethod;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
 
 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;
 
 @Column(unique = true, 
		 nullable = false)
 private String orderNumber;
 
 @ManyToOne(fetch = FetchType.LAZY)
 @JoinColumn(name = "customer_id")
 private User customer;
 
 @ManyToOne(fetch = FetchType.LAZY)
 @JoinColumn(name = "restaurant_id")
 private Restaurant restaurant;
 
 @ManyToOne(fetch = FetchType.LAZY)
 @JoinColumn(name = "driver_id")
 private DeliveryDriver driver;
 
 @OneToMany(mappedBy = "order", 
		 cascade = CascadeType.ALL)
 private List<OrderItem> items = new ArrayList<>();
 
 @Enumerated(EnumType.STRING)
 private OrderStatus status = OrderStatus.PLACED;
 
 @Enumerated(EnumType.STRING)
 private PaymentMethod paymentMethod;
 
 @ManyToOne(fetch = FetchType.LAZY)
 @JoinColumn(name = "delivery_address_id")
 private Address deliveryAddress;
 
 private Double subtotal;
 
 private Double deliveryFee;
 
 private Double tax;
 
 private Double discount = 0.0;
 
 private String couponCode;
 
 private Double totalAmount;
 
 private String specialInstructions;
 
 private LocalDateTime estimatedDeliveryTime;
 
 private LocalDateTime actualDeliveryTime;
 
 @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
 private Payment payment;
 
 @Column(updatable = false)
 private LocalDateTime createdAt;
 
 private LocalDateTime updatedAt;
 
 @PrePersist
 protected void onCreate() {
     createdAt = LocalDateTime.now();
     updatedAt = LocalDateTime.now();
     orderNumber = "ORD" + System.currentTimeMillis();
 }
 
 @PreUpdate
 protected void onUpdate() {
     updatedAt = LocalDateTime.now();
 }
}
