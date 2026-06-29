package com.fooddelivery.service;


import com.fooddelivery.dto.request.DriverLocationRequest;
import com.fooddelivery.dto.request.DriverRegistrationRequest;
import com.fooddelivery.dto.response.DriverResponse;
import com.fooddelivery.model.DeliveryDriver;
import com.fooddelivery.model.Order;
import com.fooddelivery.model.User;
import com.fooddelivery.enums.OrderStatus;
import com.fooddelivery.enums.UserRole;
import com.fooddelivery.repository.DeliveryDriverRepository;
import com.fooddelivery.repository.OrderRepository;
import com.fooddelivery.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryDriverService {

 private final DeliveryDriverRepository driverRepository;
 private final UserRepository userRepository;
 private final OrderRepository orderRepository;

 // ═══════════════════════════════════════════════
 //  1. REGISTER AS DELIVERY DRIVER
 // ═══════════════════════════════════════════════
 @Transactional
 public DeliveryDriver registerDriver(
         String email,
         DriverRegistrationRequest request) {

     // Get user account
     User user = userRepository
         .findByEmail(email)
         .orElseThrow(() ->
             new RuntimeException("User not found!"));

     // Check if already registered as driver
     if (driverRepository.existsByUserId(user.getId())) {
         throw new RuntimeException(
             "You are already registered as a driver!");
     }

     // Check user role
     if (user.getRole() != UserRole.DELIVERY_DRIVER) {
         throw new RuntimeException(
             "Your account must have DELIVERY_DRIVER role!");
     }

     // Create driver profile
     DeliveryDriver driver = DeliveryDriver.builder()
             .user(user)
             .vehicleType(request.getVehicleType())
             .vehicleNumber(request.getVehicleNumber())
             .licenseNumber(request.getLicenseNumber())
             .isAvailable(false)   // not available until verified
             .isOnline(false)
             .isVerified(false)    // admin must verify first
             .averageRating(0.0)
             .totalDeliveries(0)
             .totalEarning(0)
             .build();

     DeliveryDriver saved = driverRepository.save(driver);

     log.info("New driver registered: {} - Vehicle: {}",
         email, request.getVehicleType());

     return saved;
 }

 // ═══════════════════════════════════════════════
 //  2. UPDATE DRIVER LOCATION (GPS Tracking)
 // ═══════════════════════════════════════════════
 @Transactional
 public DeliveryDriver updateLocation(
         String email,
         DriverLocationRequest request) {

     DeliveryDriver driver = getDriverByEmail(email);

     driver.setCurrentLatitude(request.getLatitude());
     driver.setCurrentLongitude(request.getLongitude());
     //driver.setLastLocationUpdate(LocalDateTime.now());

     log.info("Driver {} location updated: {}, {}",
         email,
         request.getLatitude(),
         request.getLongitude());

     return driverRepository.save(driver);
 }

 // ═══════════════════════════════════════════════
 //  3. GO ONLINE (Driver starts working)
 // ═══════════════════════════════════════════════
 @Transactional
 public DeliveryDriver goOnline(String email) {

     DeliveryDriver driver = getDriverByEmail(email);

     // Must be verified first
     if (!driver.isVerified()) {
         throw new RuntimeException(
             "Your account is pending verification. " +
             "Please wait for admin approval.");
     }

     driver.setOnline(true);
     driver.setAvailable(true);

     log.info("Driver {} is now ONLINE ✅", email);

     return driverRepository.save(driver);
 }

 // ═══════════════════════════════════════════════
 //  4. GO OFFLINE (Driver stops working)
 // ═══════════════════════════════════════════════
 @Transactional
 public DeliveryDriver goOffline(String email) {

     DeliveryDriver driver = getDriverByEmail(email);

     // Check no active delivery
     List<Order> activeOrders = orderRepository
         .findByDriverIdOrderByCreatedAtDesc(
             driver.getId())
         .stream()
         .filter(o ->
             o.getStatus() == OrderStatus.OUT_FOR_DELIVERY)
         .toList();

     if (!activeOrders.isEmpty()) {
         throw new RuntimeException(
             "Cannot go offline! " +
             "You have an active delivery in progress.");
     }

     driver.setOnline(false);
     driver.setAvailable(false);

     log.info("Driver {} is now OFFLINE ❌", email);

     return driverRepository.save(driver);
 }

 // ═══════════════════════════════════════════════
 //  5. ACCEPT ORDER (Driver picks up delivery)
 // ═══════════════════════════════════════════════
 @Transactional
 public Order acceptOrder(String email, Long orderId) {

     DeliveryDriver driver = getDriverByEmail(email);

     // Check driver is available
     if (!driver.isAvailable() || !driver.isOnline()) {
         throw new RuntimeException(
             "You must be online and available " +
             "to accept orders!");
     }

     // Get the order
     Order order = orderRepository
         .findById(orderId)
         .orElseThrow(() ->
             new RuntimeException("Order not found!"));

     // Check order is in correct status
     if (order.getStatus() != OrderStatus.READY) {
         throw new RuntimeException(
             "Order is not ready for pickup yet! " +
             "Current status: " + order.getStatus());
     }

     // Check order has no driver yet
     if (order.getDriver() != null) {
         throw new RuntimeException(
             "This order already has a driver assigned!");
     }

     // Assign driver to order
     order.setDriver(driver);
     order.setStatus(OrderStatus.OUT_FOR_DELIVERY);

     // Mark driver as busy
     driver.setAvailable(false);

     driverRepository.save(driver);
     Order savedOrder = orderRepository.save(order);

     log.info("Driver {} accepted order {}",
         email, order.getOrderNumber());

     return savedOrder;
 }

 // ═══════════════════════════════════════════════
 //  6. MARK ORDER AS DELIVERED
 // ═══════════════════════════════════════════════
 @Transactional
 public Order markDelivered(String email, Long orderId) {

     DeliveryDriver driver = getDriverByEmail(email);

     Order order = orderRepository
         .findById(orderId)
         .orElseThrow(() ->
             new RuntimeException("Order not found!"));

     // Verify this driver owns this order
     if (order.getDriver() == null ||
         !order.getDriver().getId()
               .equals(driver.getId())) {
         throw new RuntimeException(
             "This order is not assigned to you!");
     }

     // Verify order is out for delivery
     if (order.getStatus() != OrderStatus.OUT_FOR_DELIVERY) {
         throw new RuntimeException(
             "Order is not out for delivery!");
     }

     // Mark as delivered
     order.setStatus(OrderStatus.DELIVERED);
     order.setActualDeliveryTime(LocalDateTime.now());

     // Update payment status
     if (order.getPayment() != null) {
         order.getPayment().setStatus(
             com.fooddelivery.enums.PaymentStatus.COMPLEATED);
         order.getPayment().setPaidAt(
             LocalDateTime.now());
     }

     // Update driver stats
     driver.setAvailable(true); // Free for next order
     driver.setTotalDeliveries(
         driver.getTotalDeliveries() + 1);
     driver.setTotalEarning(
         driver.getTotalEarning() +
         order.getDeliveryFee().intValue());

     driverRepository.save(driver);
     Order saved = orderRepository.save(order);

     log.info("Order {} delivered by driver {}",
         order.getOrderNumber(), email);

     return saved;
 }

 // ═══════════════════════════════════════════════
 //  7. GET DRIVER'S ACTIVE ORDER
 // ═══════════════════════════════════════════════
 public Order getActiveOrder(String email) {

     DeliveryDriver driver = getDriverByEmail(email);

     return orderRepository
         .findByDriverIdOrderByCreatedAtDesc(
             driver.getId())
         .stream()
         .filter(o ->
             o.getStatus() == OrderStatus.OUT_FOR_DELIVERY)
         .findFirst()
         .orElseThrow(() ->
             new RuntimeException(
                 "No active delivery found!"));
 }

 // ═══════════════════════════════════════════════
 //  8. GET DRIVER'S ORDER HISTORY
 // ═══════════════════════════════════════════════
 public List<Order> getOrderHistory(String email) {

     DeliveryDriver driver = getDriverByEmail(email);

     return orderRepository
         .findByDriverIdOrderByCreatedAtDesc(
             driver.getId());
 }

 // ═══════════════════════════════════════════════
 //  9. GET DRIVER PROFILE
 // ═══════════════════════════════════════════════
 public DriverResponse getProfile(String email) {

     DeliveryDriver driver = getDriverByEmail(email);

     return DriverResponse.builder()
             .driverId(driver.getId())
             .driverName(driver.getUser().getFullName())
             .vehicleType(driver.getVehicleType())
             .vehicleNumber(driver.getVehicleNumber())
             .averageRating(driver.getAverageRating())
             .totalDeliveries(driver.getTotalDeliveries())
             .currentLatitude(driver.getCurrentLatitude())
             .currentLongitude(driver.getCurrentLongitude())
             .isAvailable(driver.isAvailable())
             .isOnline(driver.isOnline())
             .isVerified(driver.isVerified())
             .build();
 }

 // ═══════════════════════════════════════════════
 //  10. GET ALL AVAILABLE DRIVERS (For Admin)
 // ═══════════════════════════════════════════════
 public List<DeliveryDriver> getAllAvailableDrivers() {
     return driverRepository.findAvailableDrivers();
 }

 // ═══════════════════════════════════════════════
 //  11. VERIFY DRIVER (Admin Only)
 // ═══════════════════════════════════════════════
 @Transactional
 public DeliveryDriver verifyDriver(Long driverId) {

     DeliveryDriver driver = driverRepository
         .findById(driverId)
         .orElseThrow(() ->
             new RuntimeException("Driver not found!"));

     driver.setVerified(true);

     log.info("Driver {} verified by admin",
         driver.getUser().getEmail());

     return driverRepository.save(driver);
 }

 // ═══════════════════════════════════════════════
 //  12. AUTO-ASSIGN NEAREST DRIVER TO ORDER
 // ═══════════════════════════════════════════════
 @Transactional
 public Order autoAssignDriver(Long orderId) {

     Order order = orderRepository
         .findById(orderId)
         .orElseThrow(() ->
             new RuntimeException("Order not found!"));

     // Get all available drivers
     List<DeliveryDriver> availableDrivers =
         driverRepository.findAvailableDrivers();

     if (availableDrivers.isEmpty()) {
         throw new RuntimeException(
             "No drivers available right now. " +
             "Please try again in a few minutes.");
     }

     // Get restaurant location
     Double restLat = order.getRestaurant().getLatitude();
     Double restLon = order.getRestaurant().getLongitude();

     // Find nearest driver using distance calculation
     DeliveryDriver nearestDriver = availableDrivers
         .stream()
         .filter(d ->
             d.getCurrentLatitude() != null &&
             d.getCurrentLongitude() != null)
         .min((d1, d2) -> {
             double dist1 = calculateDistance(
                 d1.getCurrentLatitude(),
                 d1.getCurrentLongitude(),
                 restLat, restLon);
             double dist2 = calculateDistance(
                 d2.getCurrentLatitude(),
                 d2.getCurrentLongitude(),
                 restLat, restLon);
             return Double.compare(dist1, dist2);
         })
         .orElse(availableDrivers.get(0));
         // Fallback to first driver if no location

     // Assign driver
     order.setDriver(nearestDriver);
     nearestDriver.setAvailable(false);

     driverRepository.save(nearestDriver);

     log.info("Driver {} auto-assigned to order {}",
         nearestDriver.getUser().getEmail(),
         order.getOrderNumber());

     return orderRepository.save(order);
 }

 // ═══════════════════════════════════════════════
 //  HELPER: Calculate Distance Between 2 Points
 // ═══════════════════════════════════════════════
 private double calculateDistance(
         double lat1, double lon1,
         double lat2, double lon2) {
     // Haversine formula
     double R = 6371; // Earth radius in km
     double dLat = Math.toRadians(lat2 - lat1);
     double dLon = Math.toRadians(lon2 - lon1);

     double a = Math.sin(dLat / 2) *
                Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) *
                Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) *
                Math.sin(dLon / 2);

     double c = 2 * Math.atan2(
         Math.sqrt(a), Math.sqrt(1 - a));

     return R * c; // Distance in km
 }

 // ═══════════════════════════════════════════════
 //  HELPER: Get Driver by Email
 // ═══════════════════════════════════════════════
 private DeliveryDriver getDriverByEmail(String email) {
     return driverRepository
         .findByUserEmail(email)
         .orElseThrow(() ->
             new RuntimeException(
                 "Driver profile not found! " +
                 "Please register as a driver first."));
 }
}
