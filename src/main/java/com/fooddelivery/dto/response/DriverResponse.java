package com.fooddelivery.dto.response;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DriverResponse {
 private Long driverId;
 private String driverName;
 private String vehicleType;
 private String vehicleNumber;
 private Double averageRating;
 private Integer totalDeliveries;
 private Double currentLatitude;
 private Double currentLongitude;
 private boolean isAvailable;
 private boolean isOnline;
 private boolean isVerified;
}
