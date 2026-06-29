package com.fooddelivery.dto.response;


import com.fooddelivery.enums.UserRole;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JwtResponse {
 private String token;
 private String type = "Bearer";
 private Long userId;
 private String email;
 private String fullName;
 private UserRole role;
}
