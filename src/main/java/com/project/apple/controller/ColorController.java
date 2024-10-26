package com.project.apple.controller;

import com.project.apple.model.Category;
import com.project.apple.model.Color;
import com.project.apple.service.ColorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/colors")
public class ColorController {

    @Autowired
    private ColorService colorService;

    @GetMapping
    public List<Color> getAllColors() {
        return colorService.getAllColors();
    }

    @GetMapping("/{id}")
    public Color getColorById(@PathVariable Long id) {
        return colorService.getColorById(id);
    }

    @PostMapping
    public Color createColor(@RequestBody Color color) {
        return colorService.createColor(color);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Color> updateColor(@PathVariable Long id, @RequestBody Color colorDetails) {
        return ResponseEntity.ok(colorService.updateColor(id, colorDetails));
    }

    @PatchMapping("/{id}/{active}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Color> updateColorActiveStatus(@PathVariable Long id, @PathVariable boolean active) {
        Color color = colorService.getColorById(id);
        if (color == null) {
            return ResponseEntity.notFound().build();
        }

        color.setActive(active);
        colorService.saveColor(color);
        return ResponseEntity.ok(color);
    }
}