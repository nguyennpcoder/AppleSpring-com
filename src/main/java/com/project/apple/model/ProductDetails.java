package com.project.apple.model;

import lombok.Data;
import jakarta.persistence.*;

@Entity
@Table(name = "product_details")
@Data
public class ProductDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "size_id")
    private Size size;

    @ManyToOne
    @JoinColumn(name = "color_id")
    private Color color;

    private Boolean inStock;
    private boolean isActive = true;
}