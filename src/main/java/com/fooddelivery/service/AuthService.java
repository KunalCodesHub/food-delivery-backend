package com.fooddelivery.service;

import com.fooddelivery.dto.request.LoginRequest;
import com.fooddelivery.dto.request.SignupRequest;
import com.fooddelivery.dto.response.JwtResponse;
import com.fooddelivery.model.Cart;
import com.fooddelivery.model.User;
import com.fooddelivery.enums.UserRole;
import com.fooddelivery.repository.CartRepository;
import com.fooddelivery.repository.UserRepository;
import com.fooddelivery.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication
    .AuthenticationManager;
import org.springframework.security.authentication
    .UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password
    .PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final JwtTokenProvider tokenProvider;

    @Transactional
    public JwtResponse signup(SignupRequest request) {

        // 1. Check email not already used
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException(
                "Email already registered!");
        }

        // 2. Create and save user
        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(
                    request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .role(request.getRole())
                .isActive(true)
                .build();

        user = userRepository.save(user);

        // 3. Create empty cart for customers
        if (request.getRole() == UserRole.CUSTOMER) {
            Cart cart = Cart.builder()
                    .user(user)
                    .totalAmount(0.0)
                    .build();
            cartRepository.save(cart);
        }

        // 4. Generate JWT token
        Authentication auth = authManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()));

        String token = tokenProvider.generateToken(auth);

        // 5. Return response
        return JwtResponse.builder()
                .token(token)
                .type("Bearer")
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .build();
    }

    public JwtResponse login(LoginRequest request) {

        // 1. Authenticate user
        Authentication auth = authManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()));

        // 2. Generate token
        String token = tokenProvider.generateToken(auth);

        // 3. Get user details
        User user = userRepository
            .findByEmail(request.getEmail())
            .orElseThrow(() ->
                new RuntimeException("User not found"));

        // 4. Return response
        return JwtResponse.builder()
                .token(token)
                .type("Bearer")
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .build();
    }
}
