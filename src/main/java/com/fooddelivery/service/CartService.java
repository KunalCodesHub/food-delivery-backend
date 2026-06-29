package com.fooddelivery.service;

import com.fooddelivery.dto.request.CartItemRequest;
import com.fooddelivery.model.*;
import com.fooddelivery.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final MenuItemRepository menuItemRepository;
    private final UserRepository userRepository;

    // Get cart for logged-in user
    public Cart getCart(String email) {
        User user = getUser(email);
        return cartRepository
            .findByUserId(user.getId())
            .orElseThrow(() ->
                new RuntimeException("Cart not found"));
    }

    @Transactional
    public Cart addItem(String email,
                        CartItemRequest request) {
        Cart cart = getCart(email);
        MenuItem item = menuItemRepository
            .findById(request.getMenuItemId())
            .orElseThrow(() ->
                new RuntimeException("Item not found"));

        // Check if adding from different restaurant
        if (!cart.getItems().isEmpty()) {
            Long currentRestaurantId = cart.getItems()
                .get(0)
                .getMenuItem()
                .getRestaurant()
                .getId();

            if (!currentRestaurantId.equals(
                    item.getRestaurant().getId())) {
                throw new RuntimeException(
                    "Clear cart first! " +
                    "Cannot mix items from " +
                    "different restaurants.");
            }
        }

        // Check if item already in cart
        CartItem existing = cart.getItems()
            .stream()
            .filter(ci -> ci.getMenuItem()
                .getId().equals(item.getId()))
            .findFirst()
            .orElse(null);

        if (existing != null) {
            // Increase quantity
            existing.setQuantity(
                existing.getQuantity() +
                request.getQuantity());
        } else {
            // Add new item
            Double price = item.getDiscountedPrice() != null
                ? item.getDiscountedPrice()
                : item.getPrice();

            CartItem cartItem = CartItem.builder()
                    .cart(cart)
                    .menuItem(item)
                    .quantity(request.getQuantity())
                    .price(price)
                    .specialInstructions(
                        request.getSpecialInstructions())
                    .build();
            cart.getItems().add(cartItem);
        }

        cart.calculateTotal();
        return cartRepository.save(cart);
    }

    @Transactional
    public Cart updateQuantity(String email,
                                Long cartItemId,
                                Integer quantity) {
        Cart cart = getCart(email);

        CartItem item = cart.getItems().stream()
            .filter(ci -> ci.getId().equals(cartItemId))
            .findFirst()
            .orElseThrow(() ->
                new RuntimeException(
                    "Cart item not found"));

        if (quantity <= 0) {
            cart.getItems().remove(item);
        } else {
            item.setQuantity(quantity);
        }

        cart.calculateTotal();
        return cartRepository.save(cart);
    }

    @Transactional
    public Cart clearCart(String email) {
        Cart cart = getCart(email);
        cart.getItems().clear();
        cart.setTotalAmount(0.0);
        return cartRepository.save(cart);
    }

    private User getUser(String email) {
        return userRepository
            .findByEmail(email)
            .orElseThrow(() ->
                new RuntimeException("User not found"));
    }
}
