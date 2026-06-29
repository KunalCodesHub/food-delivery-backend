package com.fooddelivery.controller;

import com.fooddelivery.dto.response.ApiResponse;
import com.fooddelivery.model.MenuItem;
import com.fooddelivery.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost
    .PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MenuController {

    private final MenuService menuService;

    // PUBLIC - View menu
    @GetMapping(
        "/api/menu/public/restaurant/{restaurantId}")
    public ResponseEntity<ApiResponse<List<MenuItem>>>
            getMenu(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(
            ApiResponse.success("Menu items",
                menuService.getMenuByRestaurant(
                    restaurantId)));
    }

    // OWNER - Add menu item
    @PostMapping(
        "/api/restaurant-owner/menu/{restaurantId}")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<ApiResponse<MenuItem>>
            addItem(@RequestBody MenuItem item,
                    @PathVariable Long restaurantId) {
        return ResponseEntity.ok(
            ApiResponse.success("Item added!",
                menuService.addMenuItem(
                    item, restaurantId)));
    }

    // OWNER - Update menu item
    @PutMapping("/api/restaurant-owner/menu/{id}")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<ApiResponse<MenuItem>>
            updateItem(@PathVariable Long id,
                       @RequestBody MenuItem item) {
        return ResponseEntity.ok(
            ApiResponse.success("Item updated!",
                menuService.updateMenuItem(id, item)));
    }

    // OWNER - Delete menu item
    @DeleteMapping("/api/restaurant-owner/menu/{id}")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<ApiResponse<String>>
            deleteItem(@PathVariable Long id) {
        menuService.deleteMenuItem(id);
        return ResponseEntity.ok(
            ApiResponse.success("Item deleted!", null));
    }
}
