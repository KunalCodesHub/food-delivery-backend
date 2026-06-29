package com.fooddelivery.repository;

import com.fooddelivery.model.DeliveryDriver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryDriverRepository
     extends JpaRepository<DeliveryDriver, Long> {

 // Find driver by their user account
 Optional<DeliveryDriver> findByUserId(Long userId);

 // Find driver by user email
 @Query("SELECT d FROM DeliveryDriver d " +
        "WHERE d.user.email = :email")
 Optional<DeliveryDriver> findByUserEmail(String email);

 // Find all available drivers
 // (online AND available AND verified)
 List<DeliveryDriver> findByIsAvailableTrueAndIsOnlineTrueAndIsVerifiedTrue();

 // Find all verified drivers
 List<DeliveryDriver> findByIsVerifiedTrue();

 // Find unverified drivers (for admin)
 List<DeliveryDriver> findByIsVerifiedFalse();

 // Find nearest available driver
 // (Simple version - gets all available drivers)
 @Query("SELECT d FROM DeliveryDriver d " +
        "WHERE d.isAvailable = true " +
        "AND d.isOnline = true " +
        "AND d.isVerified = true " +
        "ORDER BY d.averageRating DESC")
 List<DeliveryDriver> findAvailableDrivers();

 // Check if user already registered as driver
 boolean existsByUserId(Long userId);
}
