package com.fooddelivery.controller;

import com.fooddelivery.dto.request.LoginRequest;
import com.fooddelivery.dto.request.SignupRequest;
import com.fooddelivery.dto.response.ApiResponse;
import com.fooddelivery.dto.response.JwtResponse;
import com.fooddelivery.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    // Register new user
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<JwtResponse>>
            signup(@RequestBody SignupRequest request) {

        JwtResponse response = authService.signup(request);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success(
                "Registration successful!", response));
    }

    // Login
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtResponse>>
            login(@RequestBody LoginRequest request) {

        JwtResponse response = authService.login(request);
        return ResponseEntity.ok(
            ApiResponse.success(
                "Login successful!", response));
    }
}
