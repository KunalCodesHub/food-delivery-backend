package com.fooddelivery.dto.request;


import com.fooddelivery.enums.UserRole;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class SignupRequest {
 
 @NotBlank(message = "Full name is required")
 private String fullName;
 
 @Email(message = "Invalid email")
 @NotBlank(message = "Email is required")
 private String email;
 
 @NotBlank(message = "Password is required")
 @Size(min = 8, message = "Password must be at least 8 characters")
 private String password;
 
 private String phoneNumber;
 
 private UserRole role = UserRole.CUSTOMER;
}
