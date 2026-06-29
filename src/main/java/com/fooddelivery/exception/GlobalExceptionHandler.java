package com.fooddelivery.exception;


import com.fooddelivery.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

 // ─────────────────────────────────────────────
 // Handle Validation Errors
 // ─────────────────────────────────────────────
 @ExceptionHandler(MethodArgumentNotValidException.class)
 @ResponseStatus(HttpStatus.BAD_REQUEST)
 public ResponseEntity<ApiResponse<
             Map<String, String>>>
         handleValidationErrors(
             MethodArgumentNotValidException ex) {

     Map<String, String> errors = new HashMap<>();

     ex.getBindingResult()
       .getAllErrors()
       .forEach(error -> {
           String fieldName =
               ((FieldError) error).getField();
           String errorMessage =
               error.getDefaultMessage();
           errors.put(fieldName, errorMessage);
       });

     return ResponseEntity
         .status(HttpStatus.BAD_REQUEST)
         .body(ApiResponse
             .<Map<String, String>>builder()
             .success(false)
             .message("Validation failed!")
             .data(errors)
             .build());
 }

 // ─────────────────────────────────────────────
 // Handle Bad Credentials (Wrong Password)
 // ─────────────────────────────────────────────
 @ExceptionHandler(BadCredentialsException.class)
 @ResponseStatus(HttpStatus.UNAUTHORIZED)
 public ResponseEntity<ApiResponse<Void>>
         handleBadCredentials(
             BadCredentialsException ex) {

     return ResponseEntity
         .status(HttpStatus.UNAUTHORIZED)
         .body(ApiResponse.<Void>builder()
             .success(false)
             .message("Invalid email or password!")
             .build());
 }

 // ─────────────────────────────────────────────
 // Handle User Not Found
 // ─────────────────────────────────────────────
 @ExceptionHandler(UsernameNotFoundException.class)
 @ResponseStatus(HttpStatus.NOT_FOUND)
 public ResponseEntity<ApiResponse<Void>>
         handleUserNotFound(
             UsernameNotFoundException ex) {

     return ResponseEntity
         .status(HttpStatus.NOT_FOUND)
         .body(ApiResponse.<Void>builder()
             .success(false)
             .message(ex.getMessage())
             .build());
 }

 // ─────────────────────────────────────────────
 // Handle Access Denied (No Permission)
 // ─────────────────────────────────────────────
 @ExceptionHandler(AccessDeniedException.class)
 @ResponseStatus(HttpStatus.FORBIDDEN)
 public ResponseEntity<ApiResponse<Void>>
         handleAccessDenied(
             AccessDeniedException ex) {

     return ResponseEntity
         .status(HttpStatus.FORBIDDEN)
         .body(ApiResponse.<Void>builder()
             .success(false)
             .message("Access denied! " +
                      "You don't have permission " +
                      "to do this action.")
             .build());
 }

 // ─────────────────────────────────────────────
 // Handle Custom Runtime Exceptions
 // ─────────────────────────────────────────────
 @ExceptionHandler(RuntimeException.class)
 @ResponseStatus(HttpStatus.BAD_REQUEST)
 public ResponseEntity<ApiResponse<Void>>
         handleRuntimeException(
             RuntimeException ex) {

     return ResponseEntity
         .status(HttpStatus.BAD_REQUEST)
         .body(ApiResponse.<Void>builder()
             .success(false)
             .message(ex.getMessage())
             .build());
 }

 // ─────────────────────────────────────────────
 // Handle All Other Exceptions
 // ─────────────────────────────────────────────
 @ExceptionHandler(Exception.class)
 @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
 public ResponseEntity<ApiResponse<Void>>
         handleGenericException(Exception ex) {

     return ResponseEntity
         .status(HttpStatus.INTERNAL_SERVER_ERROR)
         .body(ApiResponse.<Void>builder()
             .success(false)
             .message("Something went wrong: " +
                      ex.getMessage())
             .build());
 }
}