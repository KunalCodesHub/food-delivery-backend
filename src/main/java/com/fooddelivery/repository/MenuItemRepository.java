package com.fooddelivery.repository;

import com.fooddelivery.model.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MenuItemRepository
        extends JpaRepository<MenuItem, Long> {

    List<MenuItem> findByRestaurantId(Long restaurantId);

    List<MenuItem> findByRestaurantIdAndIsAvailableTrue(
            Long restaurantId);

    List<MenuItem> findByIsBestsellerTrue();

    @Query("SELECT m FROM MenuItem m WHERE " +
           "LOWER(m.name) LIKE LOWER(CONCAT('%',:kw,'%'))")
    List<MenuItem> searchByName(@Param("kw") String keyword);
}
