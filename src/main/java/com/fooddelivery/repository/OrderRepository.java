package com.fooddelivery.repository;


import com.fooddelivery.model.Order;
import com.fooddelivery.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository
        extends JpaRepository<Order, Long> {

    List<Order> findByCustomerIdOrderByCreatedAtDesc(
            Long customerId);

    List<Order> findByRestaurantIdOrderByCreatedAtDesc(
            Long restaurantId);

    List<Order> findByRestaurantIdAndStatus(
            Long restaurantId, OrderStatus status);

    Optional<Order> findByOrderNumber(String orderNumber);
    
    List<Order> findByDriverIdOrderByCreatedAtDesc(
            Long driverId);
    
    @Query("SELECT o FROM Order o " +
            "WHERE o.driver IS NULL " +
            "AND o.status = 'READY'")
     List<Order> findUnassignedOrders();
}
