package com.fooddelivery.repository;

import com.fooddelivery.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReviewRepository
     extends JpaRepository<Review, Long> {

 List<Review> findByRestaurantIdOrderByCreatedAtDesc(
         Long restaurantId);

 
 List<Review> findByUserId(Long userId);

 @Query("SELECT AVG(r.rating) FROM Review r " +
        "WHERE r.restaurant.id = :restaurantId")
 Double getAvgRating(Long restaurantId);
}
