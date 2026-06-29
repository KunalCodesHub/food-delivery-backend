package com.fooddelivery.enums;

public enum OrderStatus {
    PLACED,        // Customer placed order
    CONFIRMED,     // Restaurant confirmed
    PREPARING,     // Being cooked
    READY,         // Ready for pickup
    OUT_FOR_DELIVERY, // Driver picked up
    DELIVERED,     // Customer received
    CANCELLED      // Order cancelled
}
