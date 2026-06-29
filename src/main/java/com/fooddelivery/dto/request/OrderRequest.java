package com.fooddelivery.dto.request;


import com.fooddelivery.enums.PaymentMethod;
import lombok.Data;

@Data
public class OrderRequest {
 private Long addressId;
 private PaymentMethod paymentMethod;
 private String couponCode;
 private String specialInstructions;
}
