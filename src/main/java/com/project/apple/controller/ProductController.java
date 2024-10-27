package com.project.apple.controller;

import com.project.apple.dto.ProductDTO;
import com.project.apple.model.Product;
import com.project.apple.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/v1/products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<Resource> getProductImage(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        if (product == null || product.getThumbnail() == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            Path imagePath = Paths.get("images").resolve(product.getThumbnail().getUrlImg()).normalize();
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
    public ResponseEntity<String> createProduct(@RequestParam("name") String name,
                                                @RequestParam("description") String description,
                                                @RequestParam("price") Double price,
                                                @RequestParam("categoryId") Long categoryId,
                                                @RequestParam("image") List<MultipartFile> images) {
        try {
            // Check if the number of images exceeds 5
            if (images.size() > 5) {
                return ResponseEntity.badRequest().body("Error: Maximum of 5 images allowed.");
            }

            // Check if the product name is unique
            if (productService.isProductNameExists(name)) {
                return ResponseEntity.badRequest().body("Error: Product name must be unique.");
            }

            if (images.isEmpty() || name.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            ProductDTO productDTO = new ProductDTO();
            productDTO.setName(name);
            productDTO.setDescription(description);
            productDTO.setPrice(price);
            productDTO.setCategoryId(categoryId);

            List<String> imageUrls = new ArrayList<>();
            for (MultipartFile image : images) {
                String imageName = image.getOriginalFilename();
                Path imagePath = Paths.get("images").resolve(imageName);
                Files.copy(image.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);
                imageUrls.add(imageName);
            }

            productDTO.setThumbnailUrl(imageUrls.get(0)); // First image as thumbnail
            productDTO.setImageUrls(imageUrls.subList(1, imageUrls.size())); // Remaining images

            productService.saveProduct(productDTO);
            return ResponseEntity.ok("Product created successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: Unable to create product.");
        }
    }


    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable Long id,
                                 @RequestParam("name") String name,
                                 @RequestParam("description") String description,
                                 @RequestParam("price") Double price,
                                 @RequestParam("categoryId") Long categoryId,
                                 @RequestParam(value = "image", required = false) MultipartFile image) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(id);
        productDTO.setName(name);
        productDTO.setDescription(description);
        productDTO.setPrice(price);
        productDTO.setCategoryId(categoryId);

        if (image != null && !image.isEmpty()) {
            String imageName = image.getOriginalFilename();
            Path imagePath = Paths.get("images").resolve(imageName);

            try {
                Files.copy(image.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);
                productDTO.setThumbnailUrl(imageName);
            } catch (Exception e) {
                throw new RuntimeException("Failed to store image", e);
            }
        }

        return productService.saveProduct(productDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }
}