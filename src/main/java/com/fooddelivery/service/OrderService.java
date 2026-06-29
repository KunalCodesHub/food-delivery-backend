package com.fooddelivery.service;

import com.fooddelivery.dto.request.OrderRequest;
import com.fooddelivery.model.*;
import com.fooddelivery.enums.OrderStatus;
import com.fooddelivery.enums.PaymentStatus;
import com.fooddelivery.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;

    @Transactional
    public Order placeOrder(String email,
                             OrderRequest request) {

        // 1. Get user
        User user = userRepository
            .findByEmail(email)
            .orElseThrow(() ->
                new RuntimeException("User not found"));

        // 2. Get user's cart
        Cart cart = cartRepository
            .findByUserId(user.getId())
            .orElseThrow(() ->
                new RuntimeException("Cart not found"));

        // 3. Validate cart is not empty
        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty!");
        }

        // 4. Get restaurant from cart
        Restaurant restaurant = cart.getItems()
            .get(0)
            .getMenuItem()
            .getRestaurant();

        // 5. Get delivery address
        Address address = user.getAddresses()
            .stream()
            .filter(a -> a.getId()
                .equals(request.getAddressId()))
            .findFirst()
            .orElseThrow(() ->
                new RuntimeException(
                    "Address not found"));

        // 6. Calculate prices
        Double subtotal = cart.getTotalAmount();
        Double deliveryFee = restaurant.getDeliveryFee();
        Double tax = subtotal * 0.05; // 5% GST
        Double discount = getDiscount(
            request.getCouponCode(), subtotal);
        Double total = subtotal + deliveryFee +
                       tax - discount;

        // 7. Create order
        Order order = Order.builder()
                .customer(user)
                .restaurant(restaurant)
                .deliveryAddress(address)
                .paymentMethod(request.getPaymentMethod())
                .subtotal(subtotal)
                .deliveryFee(deliveryFee)
                .tax(tax)
                .discount(discount)
                .couponCode(request.getCouponCode())
                .totalAmount(total)
                .specialInstructions(
                    request.getSpecialInstructions())
                .status(OrderStatus.PLACED)
                .estimatedDeliveryTime(
                    LocalDateTime.now().plusMinutes(
                        restaurant.getEstimatedDeliveryTime()))
                .build();

        // 8. Create order items from cart
        List<OrderItem> orderItems = cart.getItems()
            .stream()
            .map(cartItem -> OrderItem.builder()
                .order(order)
                .menuItem(cartItem.getMenuItem())
                .itemName(cartItem.getMenuItem().getName())
                .quantity(cartItem.getQuantity())
                .price(cartItem.getPrice())
                .totalPrice(cartItem.getPrice() *
                            cartItem.getQuantity())
                .specialInstructions(
                    cartItem.getSpecialInstructions())
                .build())
            .collect(Collectors.toList());

        order.setItems(orderItems);

        // 9. Create payment record
        Payment payment = Payment.builder()
                .order(order)
                .paymentMethod(request.getPaymentMethod())
                .amount(total)
                .status(PaymentStatus.PENDING)
                .build();

        order.setPayment(payment);

        // 10. Save order
        Order saved = orderRepository.save(order);

        // 11. Clear the cart
        cart.getItems().clear();
        cart.setTotalAmount(0.0);
        cartRepository.save(cart);

        return saved;
    }

    public List<Order> getMyOrders(String email) {
        User user = userRepository
            .findByEmail(email)
            .orElseThrow(() ->
                new RuntimeException("User not found"));
        return orderRepository
            .findByCustomerIdOrderByCreatedAtDesc(
                user.getId());
    }

    public Order getOrderById(Long id) {
        return orderRepository
            .findById(id)
            .orElseThrow(() ->
                new RuntimeException("Order not found"));
    }

    @Transactional
    public Order updateStatus(Long orderId,
                               OrderStatus status) {
        Order order = getOrderById(orderId);
        order.setStatus(status);

        if (status == OrderStatus.DELIVERED) {
            order.setActualDeliveryTime(
                LocalDateTime.now());
        }

        return orderRepository.save(order);
    }

    @Transactional
    public Order cancelOrder(Long orderId) {
        Order order = getOrderById(orderId);

        boolean canCancel =
            order.getStatus() == OrderStatus.PLACED ||
            order.getStatus() == OrderStatus.CONFIRMED;

        if (!canCancel) {
            throw new RuntimeException(
                "Cannot cancel this order!");
        }

        order.setStatus(OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }

    private Double getDiscount(String coupon,
                                Double subtotal) {
        if (coupon == null) return 0.0;
        return switch (coupon.toUpperCase()) {
            case "SAVE50" ->
                Math.min(50.0, subtotal * 0.5);
            case "FLAT100" ->
                Math.min(100.0, subtotal);
            default -> 0.0;
        };
    }
}
