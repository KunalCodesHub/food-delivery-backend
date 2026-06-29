package com.fooddelivery.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "restaurants")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Restaurant {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false)
	private String name;
	
	@Column(length = 1000)
	private String description;
	
	private String cuisineType; // e.g., Italian, Chinese, Indian, etc.
	
	@Column(nullable = false)
	private String address;
	
	private String city;
	
	private String pincode;
	
	private Double latitude; 
	
	private Double longitude;
	
	private String phoneNumber;
	
	private String email;
	
	private String coverImage;
	
	private LocalDateTime openingTime;
	
	private LocalDateTime closingTime;
	
	private Double averageRating = 0.0;
	
	private Integer totalRating = 0;
	
	private Double minimumOrderAmount = 0.0;
	
	private Double deliveryFee = 0.0;
	
	private Integer estimatedDeliveryTime; // in minutes
	
	private Boolean isActive = true;
	
	private Boolean isVeg = false; // Indicates if the restaurant is vegetarian
	
	private Boolean isFeatured = false; // Indicates if the restaurant is featured
	
	@ManyToOne(fetch = FetchType.LAZY)
	 @JsonIgnoreProperties({"addresses", "orders", "password",
            				"roles", "favorites", "phoneNumber",
            				"hibernateLazyInitializer", "handler"})
	@JoinColumn(name = "owner_id")
	private User owner; // Assuming a User entity exists for restaurant owners
	
	@JsonIgnore
	@OneToMany(mappedBy = "restaurant", 
			cascade = CascadeType.ALL)
	private List<MenuItem> menuItems = new ArrayList<>(); // Assuming a MenuItem entity exists
	
	@JsonIgnore
	@OneToMany(mappedBy = "restaurant", 
			cascade = CascadeType.ALL)
	private List<Order> orders = new ArrayList<>(); // Assuming an Order entity exists
	
	@JsonIgnore
	@OneToMany(mappedBy = "restaurant", 
			cascade = CascadeType.ALL)
	private List<Review> reviews = new ArrayList<>(); // Assuming a Review entity exists
	
	@Column(updatable = false)
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	
	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
		updatedAt = LocalDateTime.now();
		if (isActive == null) {
			isActive = true;
		}
		if (isVeg == null) {
			isVeg = false;
		}
		if (isFeatured == null) {
			isFeatured = false;
		}
	}
	
	@PreUpdate
	protected void onUpdate() {
		updatedAt = LocalDateTime.now();
	}
}
