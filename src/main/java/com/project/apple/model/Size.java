package com.project.apple.model;

import lombok.Data;
import jakarta.persistence.*;

@Entity
@Table(name = "size")
@Data
public class Size {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private boolean isActive = true;
}