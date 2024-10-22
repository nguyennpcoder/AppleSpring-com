package com.project.apple.model;

import lombok.Data;
import jakarta.persistence.*;

@Entity
@Table(name = "color")
@Data
public class Color {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
}