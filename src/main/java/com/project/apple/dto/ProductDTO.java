package com.project.apple.dto;

import lombok.Data;
import java.util.List;

@Data
public class ProductDTO {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Long categoryId;
    private List<String> imageUrls;
    private String thumbnailUrl;
    private Boolean inStock;
}