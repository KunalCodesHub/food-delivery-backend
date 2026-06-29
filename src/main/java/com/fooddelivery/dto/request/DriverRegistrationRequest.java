package com.fooddelivery.dto.request;

import lombok.Data;

@Data
public class DriverRegistrationRequest {
 private String vehicleType;    // Bike, Scooter
 private String vehicleNumber;  // KA01AB1234
 private String licenseNumber;  // DL1234567890
}
