package com.fooddelivery.dto.request;

import lombok.Data;

@Data
public class CartItemRequest {
	private Long menuItemId;
    private Integer quantity;
    private String specialInstructions;
}

