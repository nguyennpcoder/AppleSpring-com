package com.project.apple.dto;

import lombok.Data;

@Data
public class ProductDTO {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Long categoryId;
    private Long sizeId;
    private Long colorId;
    private Boolean inStock;
}