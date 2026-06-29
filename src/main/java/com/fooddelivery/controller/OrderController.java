package com.fooddelivery.controller;

import com.fooddelivery.dto.request.OrderRequest;
import com.fooddelivery.dto.response.ApiResponse;
import com.fooddelivery.model.Order;
import com.fooddelivery.enums.OrderStatus;
import com.fooddelivery.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost
    .PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderService orderService;

    // Place a new order
    @PostMapping("/place")
    public ResponseEntity<ApiResponse<Order>>
            placeOrder(@RequestBody OrderRequest request,
                       Authentication auth) {
        return ResponseEntity.ok(
            ApiResponse.success("Order placed! 🎉",
                orderService.placeOrder(
                    auth.getName(), request)));
    }

    // Get my orders
    @GetMapping("/my-orders")
    public ResponseEntity<ApiResponse<List<Order>>>
            myOrders(Authentication auth) {
        return ResponseEntity.ok(
            ApiResponse.success("Your orders",
                orderService.getMyOrders(
                    auth.getName())));
    }

    // Get single order details
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Order>>
            getOrder(@PathVariable Long id) {
        return ResponseEntity.ok(
            ApiResponse.success("Order details",
                orderService.getOrderById(id)));
    }

    // Cancel order
    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<Order>>
            cancel(@PathVariable Long id) {
        return ResponseEntity.ok(
            ApiResponse.success("Order cancelled",
                orderService.cancelOrder(id)));
    }

    // Update status (Restaurant/Admin)
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('RESTAURANT_OWNER'," +
                  "'ADMIN','DELIVERY_DRIVER')")
    public ResponseEntity<ApiResponse<Order>>
            updateStatus(@PathVariable Long id,
                         @RequestParam OrderStatus status) {
        return ResponseEntity.ok(
            ApiResponse.success("Status updated!",
                orderService.updateStatus(id, status)));
    }
}
