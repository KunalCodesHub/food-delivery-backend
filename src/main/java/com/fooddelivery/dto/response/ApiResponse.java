package com.fooddelivery.dto.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//✅ This hides null fields from JSON response
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiResponse<T> {

 private boolean success;

 private String message;

 private T data;

 // Quick success response with data
 public static <T> ApiResponse<T> success(
         String message, T data) {
     return ApiResponse.<T>builder()
             .success(true)
             .message(message)
             .data(data)
             .build();
 }

 // Quick success response without data
 public static <T> ApiResponse<T> success(
         String message) {
     return ApiResponse.<T>builder()
             .success(true)
             .message(message)
             .build();
 }

 // Quick error response
 public static <T> ApiResponse<T> error(
         String message) {
     return ApiResponse.<T>builder()
             .success(false)
             .message(message)
             .build();
 }
}