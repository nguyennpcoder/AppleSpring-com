package com.project.apple.service;

import com.project.apple.model.Category;
import com.project.apple.model.Color;
import com.project.apple.responsitory.ColorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ColorService {

    @Autowired
    private ColorRepository colorRepository;

    public List<Color> getAllColors() {
        return colorRepository.findAll();
    }

    public Color getColorById(Long id) {
        return colorRepository.findById(id).orElse(null);
    }

    public Color createColor(Color color) {
        return colorRepository.save(color);
    }

    public Color updateColor(Long id, Color colorDetails) {
        Color color = colorRepository.findById(id).orElseThrow();
        color.setName(colorDetails.getName());
        return colorRepository.save(color);
    }
    public Color saveColor(Color color) {
        return colorRepository.save(color);
    }
    public void deleteColor(Long id) {
        Color color = colorRepository.findById(id).orElse(null);
        if (color != null) {
            color.setActive(false); // Set the category as inactive
            colorRepository.save(color);
        }
    }
}