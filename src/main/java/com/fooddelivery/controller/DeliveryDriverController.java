package com.fooddelivery.controller;

import com.fooddelivery.dto.request.DriverLocationRequest;
import com.fooddelivery.dto.request.DriverRegistrationRequest;
import com.fooddelivery.dto.response.ApiResponse;
import com.fooddelivery.dto.response.DriverResponse;
import com.fooddelivery.model.DeliveryDriver;
import com.fooddelivery.model.Order;
import com.fooddelivery.service.DeliveryDriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/driver")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DeliveryDriverController {

 private final DeliveryDriverService driverService;

 // ─────────────────────────────────────────────
 // DRIVER REGISTRATION
 // ─────────────────────────────────────────────

 // Register as delivery driver
 @PostMapping("/register")
 @PreAuthorize("hasRole('DELIVERY_DRIVER')")
 public ResponseEntity<ApiResponse<DeliveryDriver>>
         register(
             @RequestBody DriverRegistrationRequest req,
             Authentication auth) {

     return ResponseEntity.ok(
         ApiResponse.success(
             "Driver registered! " +
             "Waiting for admin verification.",
             driverService.registerDriver(
                 auth.getName(), req)));
 }

 // ─────────────────────────────────────────────
 // DRIVER PROFILE
 // ─────────────────────────────────────────────

 // View own profile
 @GetMapping("/profile")
 @PreAuthorize("hasRole('DELIVERY_DRIVER')")
 public ResponseEntity<ApiResponse<DriverResponse>>
         getProfile(Authentication auth) {

     return ResponseEntity.ok(
         ApiResponse.success(
             "Driver profile",
             driverService.getProfile(
                 auth.getName())));
 }

 // ─────────────────────────────────────────────
 // ONLINE / OFFLINE STATUS
 // ─────────────────────────────────────────────

 // Go online (start working)
 @PutMapping("/go-online")
 @PreAuthorize("hasRole('DELIVERY_DRIVER')")
 public ResponseEntity<ApiResponse<DeliveryDriver>>
         goOnline(Authentication auth) {

     return ResponseEntity.ok(
         ApiResponse.success(
             "You are now ONLINE! ✅ " +
             "Ready to receive orders.",
             driverService.goOnline(
                 auth.getName())));
 }

 // Go offline (stop working)
 @PutMapping("/go-offline")
 @PreAuthorize("hasRole('DELIVERY_DRIVER')")
 public ResponseEntity<ApiResponse<DeliveryDriver>>
         goOffline(Authentication auth) {

     return ResponseEntity.ok(
         ApiResponse.success(
             "You are now OFFLINE ❌",
             driverService.goOffline(
                 auth.getName())));
 }

 // ─────────────────────────────────────────────
 // LOCATION TRACKING
 // ─────────────────────────────────────────────

 // Update GPS location
 @PutMapping("/update-location")
 @PreAuthorize("hasRole('DELIVERY_DRIVER')")
 public ResponseEntity<ApiResponse<DeliveryDriver>>
         updateLocation(
             @RequestBody DriverLocationRequest request,
             Authentication auth) {

     return ResponseEntity.ok(
         ApiResponse.success(
             "Location updated 📍",
             driverService.updateLocation(
                 auth.getName(), request)));
 }

 // ─────────────────────────────────────────────
 // ORDER MANAGEMENT
 // ─────────────────────────────────────────────

 // Accept an order
 @PutMapping("/accept-order/{orderId}")
 @PreAuthorize("hasRole('DELIVERY_DRIVER')")
 public ResponseEntity<ApiResponse<Order>>
         acceptOrder(
             @PathVariable Long orderId,
             Authentication auth) {

     return ResponseEntity.ok(
         ApiResponse.success(
             "Order accepted! 🛵 " +
             "Head to the restaurant.",
             driverService.acceptOrder(
                 auth.getName(), orderId)));
 }

 // Mark order as delivered
 @PutMapping("/mark-delivered/{orderId}")
 @PreAuthorize("hasRole('DELIVERY_DRIVER')")
 public ResponseEntity<ApiResponse<Order>>
         markDelivered(
             @PathVariable Long orderId,
             Authentication auth) {

     return ResponseEntity.ok(
         ApiResponse.success(
             "Order marked as DELIVERED! ✅ " +
             "Great job!",
             driverService.markDelivered(
                 auth.getName(), orderId)));
 }

 // Get current active delivery
 @GetMapping("/active-order")
 @PreAuthorize("hasRole('DELIVERY_DRIVER')")
 public ResponseEntity<ApiResponse<Order>>
         getActiveOrder(Authentication auth) {

     return ResponseEntity.ok(
         ApiResponse.success(
             "Active delivery",
             driverService.getActiveOrder(
                 auth.getName())));
 }

 // Get delivery history
 @GetMapping("/order-history")
 @PreAuthorize("hasRole('DELIVERY_DRIVER')")
 public ResponseEntity<ApiResponse<List<Order>>>
         getHistory(Authentication auth) {

     return ResponseEntity.ok(
         ApiResponse.success(
             "Your delivery history",
             driverService.getOrderHistory(
                 auth.getName())));
 }

 // ─────────────────────────────────────────────
 // ADMIN ENDPOINTS
 // ─────────────────────────────────────────────

 // Admin: Verify a driver
 @PutMapping("/admin/verify/{driverId}")
 @PreAuthorize("hasRole('ADMIN')")
 public ResponseEntity<ApiResponse<DeliveryDriver>>
         verifyDriver(@PathVariable Long driverId) {

     return ResponseEntity.ok(
         ApiResponse.success(
             "Driver verified successfully! ✅",
             driverService.verifyDriver(driverId)));
 }

 // Admin: Get all available drivers
 @GetMapping("/admin/available")
 @PreAuthorize("hasRole('ADMIN')")
 public ResponseEntity<ApiResponse<
             List<DeliveryDriver>>>
         getAvailableDrivers() {

     return ResponseEntity.ok(
         ApiResponse.success(
             "Available drivers",
             driverService.getAllAvailableDrivers()));
 }

 // Admin: Auto-assign driver to order
 @PutMapping("/admin/auto-assign/{orderId}")
 @PreAuthorize("hasRole('ADMIN')")
 public ResponseEntity<ApiResponse<Order>>
         autoAssign(@PathVariable Long orderId) {

     return ResponseEntity.ok(
         ApiResponse.success(
             "Driver auto-assigned! 🛵",
             driverService.autoAssignDriver(orderId)));
 }
}