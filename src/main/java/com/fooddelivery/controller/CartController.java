package com.fooddelivery.controller;

import com.fooddelivery.dto.request.CartItemRequest;
import com.fooddelivery.dto.response.ApiResponse;
import com.fooddelivery.model.Cart;
import com.fooddelivery.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CartController {

    private final CartService cartService;

    // View cart
    @GetMapping
    public ResponseEntity<ApiResponse<Cart>>
            getCart(Authentication auth) {
        return ResponseEntity.ok(
            ApiResponse.success("Your cart",
                cartService.getCart(auth.getName())));
    }

    // Add item to cart
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<Cart>>
            addItem(@RequestBody CartItemRequest request,
                    Authentication auth) {
        return ResponseEntity.ok(
            ApiResponse.success("Item added!",
                cartService.addItem(
                    auth.getName(), request)));
    }

    // Update quantity
    @PutMapping("/update/{cartItemId}")
    public ResponseEntity<ApiResponse<Cart>>
            updateQty(@PathVariable Long cartItemId,
                      @RequestParam Integer quantity,
                      Authentication auth) {
        return ResponseEntity.ok(
            ApiResponse.success("Cart updated!",
                cartService.updateQuantity(
                    auth.getName(),
                    cartItemId,
                    quantity)));
    }

    // Clear entire cart
    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse<Cart>>
            clearCart(Authentication auth) {
        return ResponseEntity.ok(
            ApiResponse.success("Cart cleared!",
                cartService.clearCart(
                    auth.getName())));
    }
}
