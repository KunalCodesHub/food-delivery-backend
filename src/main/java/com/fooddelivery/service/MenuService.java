package com.fooddelivery.service;

import com.fooddelivery.model.MenuItem;
import com.fooddelivery.model.Restaurant;
import com.fooddelivery.repository.MenuItemRepository;
import com.fooddelivery.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuItemRepository menuItemRepository;
    private final RestaurantRepository restaurantRepository;

    public List<MenuItem> getMenuByRestaurant(
            Long restaurantId) {
        return menuItemRepository
            .findByRestaurantIdAndIsAvailableTrue(
                restaurantId);
    }

    @Transactional
    public MenuItem addMenuItem(MenuItem item,
                                Long restaurantId) {
        Restaurant restaurant = restaurantRepository
            .findById(restaurantId)
            .orElseThrow(() ->
                new RuntimeException(
                    "Restaurant not found"));

        item.setRestaurant(restaurant);
        return menuItemRepository.save(item);
    }

    @Transactional
    public MenuItem updateMenuItem(Long id,
                                   MenuItem updated) {
        MenuItem item = menuItemRepository
            .findById(id)
            .orElseThrow(() ->
                new RuntimeException("Item not found"));

        item.setName(updated.getName());
        item.setDescription(updated.getDescription());
        item.setPrice(updated.getPrice());
        item.setIsAvailable(updated.getIsAvailable());

        return menuItemRepository.save(item);
    }

    @Transactional
    public void deleteMenuItem(Long id) {
        menuItemRepository.deleteById(id);
    }
}