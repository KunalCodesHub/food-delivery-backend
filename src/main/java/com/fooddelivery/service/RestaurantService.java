package com.fooddelivery.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fooddelivery.dto.request.RestaurantRequest;
import com.fooddelivery.dto.response.RestaurantResponse;
import com.fooddelivery.model.Restaurant;
import com.fooddelivery.model.User;
import com.fooddelivery.repository.RestaurantRepository;
import com.fooddelivery.repository
    .UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RestaurantService {

    private final RestaurantRepository
        restaurantRepository;
    private final UserRepository userRepository;


    @Transactional(readOnly = true)
    public List<Restaurant> searchRestaurants(String keyword) {
        return restaurantRepository.searchRestaurants(keyword);
    }


    @Transactional
    public RestaurantResponse create(
            RestaurantRequest request,
            String ownerEmail) {

        // Step 1: Find owner
        User owner = userRepository
            .findByEmail(ownerEmail)
            .orElseThrow(() ->
                new RuntimeException(
                    "Owner not found!"));

        // Step 2: Build restaurant
        Restaurant restaurant = Restaurant.builder()
            .name(request.getName())
            .description(request.getDescription())
            .cuisineType(request.getCuisineType())
            .address(request.getAddress())
            .city(request.getCity())
            .pincode(request.getPincode())
            .latitude(request.getLatitude())
            .longitude(request.getLongitude())
            .phoneNumber(request.getPhoneNumber())
            .email(request.getEmail())
            .coverImage(request.getCoverImage())
            .openingTime(request.getOpeningTime())
            .closingTime(request.getClosingTime())
            .minimumOrderAmount(
                request.getMinimumOrderAmount() != null
                    ? request.getMinimumOrderAmount()
                    : 0.0)
            .deliveryFee(
                request.getDeliveryFee() != null
                    ? request.getDeliveryFee()
                    : 0.0)
            .estimatedDeliveryTime(
                request.getEstimatedDeliveryTime())
            .isVeg(
                request.getIsVeg() != null
                    ? request.getIsVeg()
                    : false)
            .isActive(true)
            .isFeatured(false)
            .averageRating(0.0)
            .totalRating(0)
            .owner(owner)
            .build();

        // Step 3: Save
        Restaurant saved =
            restaurantRepository.save(restaurant);

        log.info("Restaurant created: {} by {}",
            saved.getName(), ownerEmail);

        // Step 4: Convert to DTO and return
        return mapToResponse(saved);
    }

    
    @Transactional(readOnly = true)
    public List<RestaurantResponse> getAllRestaurants() {

        return restaurantRepository
            .findByIsActiveTrue()
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    
   
    @Transactional(readOnly = true)
    public RestaurantResponse getById(Long id) {

        Restaurant restaurant = restaurantRepository
            .findById(id)
            .orElseThrow(() ->
                new RuntimeException(
                    "Restaurant not found!"));

        return mapToResponse(restaurant);
    }

    
    @Transactional
    public RestaurantResponse update(
            Long id,
            RestaurantRequest request,
            String ownerEmail) {

        Restaurant restaurant = restaurantRepository
            .findById(id)
            .orElseThrow(() ->
                new RuntimeException(
                    "Restaurant not found!"));

        // Check ownership
        if (!restaurant.getOwner()
                       .getEmail()
                       .equals(ownerEmail)) {
            throw new RuntimeException(
                "You can only update " +
                "your own restaurant!");
        }

        // Update fields
        restaurant.setName(request.getName());
        restaurant.setDescription(
            request.getDescription());
        restaurant.setCuisineType(
            request.getCuisineType());
        restaurant.setAddress(request.getAddress());
        restaurant.setCity(request.getCity());
        restaurant.setPincode(request.getPincode());
        restaurant.setPhoneNumber(
            request.getPhoneNumber());
        restaurant.setMinimumOrderAmount(
            request.getMinimumOrderAmount());
        restaurant.setDeliveryFee(
            request.getDeliveryFee());
        restaurant.setEstimatedDeliveryTime(
            request.getEstimatedDeliveryTime());

        if (request.getIsVeg() != null) {
            restaurant.setIsVeg(request.getIsVeg());
        }

        Restaurant updated =
            restaurantRepository.save(restaurant);

        return mapToResponse(updated);
    }

   
    @Transactional(readOnly = true)
    public List<RestaurantResponse> getMyRestaurants(
            String ownerEmail) {

        User owner = userRepository
            .findByEmail(ownerEmail)
            .orElseThrow(() ->
                new RuntimeException(
                    "Owner not found!"));

        return restaurantRepository
            .findByOwnerId(owner.getId())
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

   
    private RestaurantResponse mapToResponse(
            Restaurant restaurant) {

        return RestaurantResponse.builder()
            .id(restaurant.getId())
            .name(restaurant.getName())
            .description(restaurant.getDescription())
            .cuisineType(restaurant.getCuisineType())
            .address(restaurant.getAddress())
            .city(restaurant.getCity())
            .pincode(restaurant.getPincode())
            .latitude(restaurant.getLatitude())
            .longitude(restaurant.getLongitude())
            .phoneNumber(restaurant.getPhoneNumber())
            .email(restaurant.getEmail())
            .coverImage(restaurant.getCoverImage())
            .openingTime(restaurant.getOpeningTime())
            .closingTime(restaurant.getClosingTime())
            .averageRating(
                restaurant.getAverageRating())
            .totalRating(restaurant.getTotalRating())
            .minimumOrderAmount(
                restaurant.getMinimumOrderAmount())
            .deliveryFee(restaurant.getDeliveryFee())
            .estimatedDeliveryTime(
                restaurant.getEstimatedDeliveryTime())
            .isActive(restaurant.getIsActive())
            .isVeg(restaurant.getIsVeg())
            .isFeatured(restaurant.getIsFeatured())
            // ✅ Only get owner info, not full object
            .ownerId(restaurant.getOwner().getId())
            .ownerName(
                restaurant.getOwner().getFullName())
            .ownerEmail(
                restaurant.getOwner().getEmail())
            .createdAt(restaurant.getCreatedAt())
            .updatedAt(restaurant.getUpdatedAt())
            .build();
    }
}