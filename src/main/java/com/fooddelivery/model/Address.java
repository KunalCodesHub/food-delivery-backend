package com.fooddelivery.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "addresses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String label; // Home, Work, Other

    @Column(nullable = false)
    private String streetAddress;

    private String city;

    private String state;

    private String pincode;

    private String landmark;

    private Double latitude;

    private Double longitude;

    private boolean isDefault = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
