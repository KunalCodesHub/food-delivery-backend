package com.fooddelivery.controller;

import com.fooddelivery.dto.request.ReviewRequest;
import com.fooddelivery.dto.response.ApiResponse;
import com.fooddelivery.model.Review;
import com.fooddelivery.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReviewController {

 private final ReviewService reviewService;

 // Add a review
 @PostMapping
 public ResponseEntity<ApiResponse<Review>>
         addReview(
             @RequestBody ReviewRequest request,
             Authentication auth) {

     return ResponseEntity.ok(
         ApiResponse.success(
             "Review added! ⭐",
             reviewService.addReview(
                 auth.getName(),
                 request.getRestaurantId(),
                 request.getOrderId(),
                 request.getRating(),
                 request.getComment())));
 }

 // Get reviews for a restaurant
 @GetMapping("/restaurant/{restaurantId}")
 public ResponseEntity<ApiResponse<List<Review>>>
         getRestaurantReviews(
             @PathVariable Long restaurantId) {

     return ResponseEntity.ok(
         ApiResponse.success(
             "Restaurant reviews",
             reviewService.getRestaurantReviews(
                 restaurantId)));
 }

 // Get my reviews
 @GetMapping("/my-reviews")
 public ResponseEntity<ApiResponse<List<Review>>>
         getMyReviews(Authentication auth) {

     return ResponseEntity.ok(
         ApiResponse.success(
             "My reviews",
             reviewService.getMyReviews(
                 auth.getName())));
 }

 // Delete my review
 @DeleteMapping("/{reviewId}")
 public ResponseEntity<ApiResponse<String>>
         deleteReview(
             @PathVariable Long reviewId,
             Authentication auth) {

     reviewService.deleteReview(
         reviewId, auth.getName());

     return ResponseEntity.ok(
         ApiResponse.success(
             "Review deleted!", null));
 }
}
