package com.project.apple.model;

import lombok.Data;
import jakarta.persistence.*;

@Entity
@Table(name = "category")
@Data
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String cateimg; // New field for image path
    @Column(name = "is_active")
    private boolean active  = true;
}