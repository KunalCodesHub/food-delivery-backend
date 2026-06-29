package com.fooddelivery.dto.response;


import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RestaurantResponse {

    private Long id;

    private String name;

    private String description;

    private String cuisineType;

    private String address;

    private String city;

    private String pincode;

    private Double latitude;

    private Double longitude;

    private String phoneNumber;

    private String email;

    private String coverImage;

    private LocalDateTime openingTime;

    private LocalDateTime closingTime;

    private Double averageRating;

    private Integer totalRating;

    private Double minimumOrderAmount;

    private Double deliveryFee;

    private Integer estimatedDeliveryTime;

    private Boolean isActive;

    private Boolean isVeg;

    private Boolean isFeatured;

    // ✅ Only owner ID and name (not full User object)
    private Long ownerId;

    private String ownerName;

    private String ownerEmail;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
