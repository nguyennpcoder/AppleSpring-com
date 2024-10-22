package com.project.apple.dto;

import lombok.Data;

@Data
public class CategoryDTO {
    private Long id;
    private String name;
    private String cateimg; // New field for image path
}