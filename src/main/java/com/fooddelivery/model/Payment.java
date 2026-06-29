package com.fooddelivery.model;

import com.fooddelivery.enums.PaymentMethod;
import com.fooddelivery.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {
 
 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;
 
 @OneToOne
 @JoinColumn(name = "order_id")
 private Order order;
 
 private String transactionId;
 
 @Enumerated(EnumType.STRING)
 private PaymentMethod paymentMethod;
 
 @Enumerated(EnumType.STRING)
 private PaymentStatus status = PaymentStatus.PENDING;
 
 private Double amount;
 
 private String razorpayOrderId;
 
 private String razorpayPaymentId;
 
 private String razorpaySignature;
 
 private LocalDateTime paidAt;
 
 @Column(updatable = false)
 private LocalDateTime createdAt;
 
 @PrePersist
 protected void onCreate() {
     createdAt = LocalDateTime.now();
 }
}
