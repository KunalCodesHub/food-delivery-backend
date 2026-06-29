package com.fooddelivery.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fooddelivery.model.Restaurant;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    List<Restaurant> findByIsActiveTrue();

    List<Restaurant> findByCityAndIsActiveTrue(String city);

    List<Restaurant> findByIsFeaturedTrue();

      @Query("""
        SELECT DISTINCT r FROM Restaurant r
        LEFT JOIN FETCH r.owner o
        WHERE r.isActive = true
        AND (
            LOWER(r.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(r.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(r.cuisineType) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(r.city) LIKE LOWER(CONCAT('%', :keyword, '%'))
        )
    """)
    List<Restaurant> searchRestaurants(@Param("keyword") String keyword);

    List<Restaurant> findByOwnerId(Long ownerId);
}
