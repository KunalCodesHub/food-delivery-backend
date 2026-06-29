package com.fooddelivery.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "delivery_drivers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryDriver {
 
 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;
 
 @OneToOne
 @JoinColumn(name = "user_id")
 private User user;
 
 private String vehicleType;
 
 private String vehicleNumber;
 
 private String licenseNumber;
 
 private Double currentLatitude;
 
 private Double currentLongitude;
 
 private boolean isAvailable = true;
 
 private boolean isOnline = false;
 
 private boolean isVerified = false;
 
 private Double averageRating = 0.0;
 
 private Integer totalRating = 0;
 
 private Integer totalDeliveries = 0;
 
 private Integer totalEarning = 0;
 
 @OneToMany(mappedBy = "driver",
         fetch = FetchType.LAZY)
 private List<Order> assignedOrders = new ArrayList<>();
 
 @Column(updatable = false)
 private LocalDateTime createdAt;

 private LocalDateTime updatedAt;

 @PrePersist
 protected void onCreate() {
     createdAt = LocalDateTime.now();
     updatedAt = LocalDateTime.now();
 }

 @PreUpdate
 protected void onUpdate() {
     updatedAt = LocalDateTime.now();
 }
}