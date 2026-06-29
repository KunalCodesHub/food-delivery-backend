package com.fooddelivery.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fooddelivery.dto.request.RestaurantRequest;
import com.fooddelivery.dto.response.ApiResponse;
import com.fooddelivery.dto.response.RestaurantResponse;
import com.fooddelivery.model.Restaurant;
import com.fooddelivery.service.RestaurantService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RestaurantController {

    private final RestaurantService restaurantService;

    // ─────────────────────────────────────────
    // PUBLIC - Get all restaurants
    // ─────────────────────────────────────────
    @GetMapping("/api/restaurants/public/all")
    public ResponseEntity<ApiResponse<
                List<RestaurantResponse>>>
            getAll() {
        return ResponseEntity.ok(
            ApiResponse.success(
                "All restaurants",
                restaurantService.getAllRestaurants()));
    }

    // ─────────────────────────────────────────
    // PUBLIC - Get restaurant by ID
    // ─────────────────────────────────────────
    @GetMapping("/api/restaurants/public/{id}")
    public ResponseEntity<ApiResponse<
                RestaurantResponse>>
            getById(@PathVariable Long id) {
        return ResponseEntity.ok(
            ApiResponse.success(
                "Restaurant details",
                restaurantService.getById(id)));
    }

    @GetMapping("/api/restaurants/public/search")
    public ResponseEntity<ApiResponse<List<Restaurant>>> searchRestaurants(
            @RequestParam String keyword) {

        List<Restaurant> restaurants = restaurantService.searchRestaurants(keyword);

        return ResponseEntity.ok(
                ApiResponse.success("Restaurants found", restaurants)
        );
    }
    
    // ─────────────────────────────────────────
    // OWNER - Create restaurant
    // ─────────────────────────────────────────
    @PostMapping("/api/restaurant-owner/restaurant")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<ApiResponse<
                RestaurantResponse>>
            create(
                @RequestBody RestaurantRequest request,
                Authentication auth) {

        return ResponseEntity.ok(
            ApiResponse.success(
                "Restaurant created!",
                restaurantService.create(
                    request, auth.getName())));
    }

    // ─────────────────────────────────────────
    // OWNER - Update restaurant
    // ─────────────────────────────────────────
    @PutMapping(
        "/api/restaurant-owner/restaurant/{id}")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<ApiResponse<
                RestaurantResponse>>
            update(
                @PathVariable Long id,
                @RequestBody RestaurantRequest request,
                Authentication auth) {

        return ResponseEntity.ok(
            ApiResponse.success(
                "Restaurant updated!",
                restaurantService.update(
                    id, request, auth.getName())));
    }

    // ─────────────────────────────────────────
    // OWNER - Get my restaurants
    // ─────────────────────────────────────────
    @GetMapping(
        "/api/restaurant-owner/my-restaurants")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<ApiResponse<
                List<RestaurantResponse>>>
            getMyRestaurants(Authentication auth) {

        return ResponseEntity.ok(
            ApiResponse.success(
                "My restaurants",
                restaurantService.getMyRestaurants(
                    auth.getName())));
    }
}