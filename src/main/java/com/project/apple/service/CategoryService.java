package com.project.apple.service;

import com.project.apple.model.Category;
import com.project.apple.responsitory.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> getCategoriesForUser(String role) {
        // If the user is an admin, return all categories
        if ("ROLE_admin".equals(role)) {
            return categoryRepository.findAll();  // Admin sees all categories
        } else {
            // Regular user or unauthenticated requests only see active categories
            return categoryRepository.findByActiveTrue();
        }
    }
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id).orElse(null);
    }

    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }

    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id).orElse(null);
        if (category != null) {
            category.setActive(false);
            categoryRepository.save(category);
        }
    }
}