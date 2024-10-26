package com.project.apple.controller;

import com.project.apple.model.Category;
import com.project.apple.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@RestController
@RequestMapping("api/v1/categories")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<Category>> getCategories(Authentication authentication) {
        // Check the roles of the authenticated user
        String role = authentication != null ? authentication.getAuthorities().iterator().next().getAuthority() : "ROLE_user";

        // Call the service method to get categories based on role
        List<Category> categories = categoryService.getCategoriesForUser(role);

        return ResponseEntity.ok(categories);
    }
    @GetMapping("/{id}")
    public Category getCategoryById(@PathVariable Long id) {
        return categoryService.getCategoryById(id);
    }
//http://localhost:8080/images/url img .png
    @GetMapping("/{id}/image") // vì sao sử dụng url trên mà không phải api/v1/categories/{id}/image/url img .png
    // vì sử dụng tên miền đầu tiên để ánh xạ trực tiếp vào file images trong src, còn tên miền bên dưới là chỉ ánh xạ đến url ảnh trong database
    public ResponseEntity<Resource> getCategoryImage(@PathVariable Long id) {
        Category category = categoryService.getCategoryById(id);
        if (category == null || category.getCateimg() == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            Path imagePath = Paths.get("images").resolve(category.getCateimg()).normalize();
            Resource resource = new UrlResource(imagePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    public Category createCategory(@RequestParam("name") String name,
                                   @RequestParam("image") MultipartFile image) {
        String imageName = image.getOriginalFilename();
        Path imagePath = Paths.get("images").resolve(imageName);

        try {
            Files.copy(image.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            throw new RuntimeException("Failed to store image", e);
        }

        Category category = new Category();
        category.setName(name);
        category.setCateimg(imageName);

        return categoryService.saveCategory(category);
    }

    @PutMapping("/{id}")
    public Category updateCategory(@PathVariable Long id,
                                   @RequestParam("name") String name,
                                   @RequestParam(value = "image", required = false) MultipartFile image) {
        Category category = categoryService.getCategoryById(id);
        if (category == null) {
            throw new RuntimeException("Category not found");
        }

        category.setName(name);

        if (image != null && !image.isEmpty()) {
            String imageName = image.getOriginalFilename();
            Path imagePath = Paths.get("images").resolve(imageName);

            try {
                Files.copy(image.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);
                category.setCateimg(imageName);
            } catch (Exception e) {
                throw new RuntimeException("Failed to store image", e);
            }
        }

        return categoryService.saveCategory(category);
    }

    @PatchMapping("/{id}/{active}")
    public ResponseEntity<Category> updateCategoryActiveStatus(@PathVariable Long id, @PathVariable boolean active) {
        Category category = categoryService.getCategoryById(id);
        if (category == null) {
            return ResponseEntity.notFound().build();
        }

        category.setActive(active);
        categoryService.saveCategory(category);
        return ResponseEntity.ok(category);
    }


}