
package com.fooddelivery.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "menu_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(length = 500)
    private String description;
    
    @Column(nullable = false)
    private Double price;
    
    private Double discountedPrice;
    
    private String image;
    
    private Boolean isVeg = true;
    
    private Boolean isBestseller = false;
    
    private Boolean isAvailable = true;
    
    private Integer preparationTime;  // in minutes
    
    // Nutritional info (AI can help populate this)
    private Integer calories;
    private String ingredients;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
    
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (isVeg == null) {
            isVeg = true;
        }
        if (isBestseller == null) {
            isBestseller = false;
        }
        if (isAvailable == null) {
            isAvailable = true;
        }
    }
}