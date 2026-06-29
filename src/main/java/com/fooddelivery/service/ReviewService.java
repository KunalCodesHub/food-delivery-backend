package com.fooddelivery.service;


import com.fooddelivery.model.Order;
import com.fooddelivery.model.Restaurant;
import com.fooddelivery.model.Review;
import com.fooddelivery.model.User;
import com.fooddelivery.repository.OrderRepository;
import com.fooddelivery.repository.RestaurantRepository;
import com.fooddelivery.repository.ReviewRepository;
import com.fooddelivery.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

 private final ReviewRepository reviewRepository;
 private final RestaurantRepository restaurantRepository;
 private final UserRepository userRepository;
 private final OrderRepository orderRepository;

 
 @Transactional
 public Review addReview(
         String email,
         Long restaurantId,
         Long orderId,
         Integer rating,
         String comment) {

     // Get user
     User user = userRepository
         .findByEmail(email)
         .orElseThrow(() ->
             new RuntimeException("User not found!"));

     // Get restaurant
     Restaurant restaurant = restaurantRepository
         .findById(restaurantId)
         .orElseThrow(() ->
             new RuntimeException(
                 "Restaurant not found!"));

     // Get order
     Order order = orderRepository
         .findById(orderId)
         .orElseThrow(() ->
             new RuntimeException("Order not found!"));

     // Validate rating (1 to 5 only)
     if (rating < 1 || rating > 5) {
         throw new RuntimeException(
             "Rating must be between 1 and 5!");
     }

     // Create review (NO AI sentiment)
     Review review = Review.builder()
             .user(user)
             .restaurant(restaurant)
             .order(order)
             .rating(rating)
             .comment(comment)
             .build();

     review = reviewRepository.save(review);

     // Update restaurant average rating
     updateRestaurantRating(restaurant, restaurantId);

     return review;
 }

 // Get all reviews for a restaurant
 public List<Review> getRestaurantReviews(
         Long restaurantId) {
     return reviewRepository
         .findByRestaurantIdOrderByCreatedAtDesc(
             restaurantId);
 }

 // Get my reviews
 public List<Review> getMyReviews(String email) {
     User user = userRepository
         .findByEmail(email)
         .orElseThrow(() ->
             new RuntimeException("User not found!"));

     return reviewRepository
         .findByUserId(user.getId());
 }

 // Delete review
 @Transactional
 public void deleteReview(Long reviewId, String email) {
     Review review = reviewRepository
         .findById(reviewId)
         .orElseThrow(() ->
             new RuntimeException("Review not found!"));

     // Check it belongs to this user
     if (!review.getUser()
                .getEmail()
                .equals(email)) {
         throw new RuntimeException(
             "You can only delete your own reviews!");
     }

     reviewRepository.delete(review);
 }

 // Helper: Update restaurant rating
 private void updateRestaurantRating(
         Restaurant restaurant,
         Long restaurantId) {

     Double avgRating = reviewRepository
         .getAvgRating(restaurantId);

     restaurant.setAverageRating(
         avgRating != null ? avgRating : 0.0);

     restaurant.setTotalRating(
         restaurant.getTotalRating() + 1);

     restaurantRepository.save(restaurant);
 }
}
