package com.fooddelivery.dto.request;

import lombok.Data;

@Data
public class ReviewRequest {
 private Long restaurantId;
 private Long orderId;
 private Integer rating;   // 1 to 5
 private String comment;
}
