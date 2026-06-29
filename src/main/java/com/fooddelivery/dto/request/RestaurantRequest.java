package com.fooddelivery.dto.request;


import java.time.LocalDateTime;

import lombok.Data;


@Data
public class RestaurantRequest {

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

    private Double minimumOrderAmount;

    private Double deliveryFee;

    private Integer estimatedDeliveryTime;

    private Boolean isVeg;
}