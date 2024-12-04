package com.project.apple.controller;

import com.project.apple.dto.ProductDTO;
import com.project.apple.model.Product;
import com.project.apple.model.ProductImg;
import com.project.apple.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

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

            if (Files.exists(imagePath) && resource.isReadable()) {
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

    private String generateRandomFileName(String originalFileName) {
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf('.'));
        String baseName = originalFileName.substring(0, originalFileName.lastIndexOf('.'));
        String randomCode10_1 = String.format("%010d", Math.abs(new Random().nextLong() % 10000000000L));
        String randomCode10_2 = String.format("%010d", Math.abs(new Random().nextLong() % 10000000000L));
        String randomCode20 = randomCode10_1 + randomCode10_2;
        return baseName  + "__________" + randomCode20 + fileExtension;
    }

    @PostMapping
    public ResponseEntity<String> createProduct(@RequestParam("name") String name,
                                                @RequestParam("description") String description,
                                                @RequestParam("price") Double price,
                                                @RequestParam("categoryId") Long categoryId,
                                                @RequestParam("image") List<MultipartFile> images) {
        try {
            if (images.size() > 5) {
                return ResponseEntity.badRequest().body("Error: Maximum of 5 images allowed.");
            }

            if (productService.isProductNameExists(name)) {
                return ResponseEntity.badRequest().body("Error: Product name must be unique.");
            }

            ProductDTO productDTO = new ProductDTO();
            productDTO.setName(name);
            productDTO.setDescription(description);
            productDTO.setPrice(price);
            productDTO.setCategoryId(categoryId);

            List<String> imageUrls = new ArrayList<>();
            for (MultipartFile image : images) {
                String imageName = generateRandomFileName(image.getOriginalFilename());
                Path imagePath = Paths.get("images").resolve(imageName);
                Files.copy(image.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);
                imageUrls.add(imageName);
            }

            productDTO.setThumbnailUrl(imageUrls.get(0));
            productDTO.setImageUrls(imageUrls.subList(1, imageUrls.size()));

            productService.saveProduct(productDTO);
            return ResponseEntity.ok("Product created successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: Unable to save images.");
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: Unable to create product.");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateProduct(@PathVariable Long id,
                                                @RequestParam("name") String name,
                                                @RequestParam("description") String description,
                                                @RequestParam("price") Double price,
                                                @RequestParam("categoryId") Long categoryId,
                                                @RequestParam(value = "image", required = false) List<MultipartFile> images) {
        try {
            ProductDTO productDTO = new ProductDTO();
            productDTO.setId(id);
            productDTO.setName(name);
            productDTO.setDescription(description);
            productDTO.setPrice(price);
            productDTO.setCategoryId(categoryId);

            if (images != null && !images.isEmpty()) {
                List<String> imageUrls = new ArrayList<>();
                for (MultipartFile image : images) {
                    String imageName = generateRandomFileName(image.getOriginalFilename());
                    Path imagePath = Paths.get("images").resolve(imageName);
                    Files.copy(image.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);
                    imageUrls.add(imageName);
                }

                productDTO.setThumbnailUrl(imageUrls.get(0));
                productDTO.setImageUrls(imageUrls.subList(1, imageUrls.size()));
            } else {
                // Retain existing images if no new images are provided
                Product existingProduct = productService.getProductById(id);
                if (existingProduct != null) {
                    productDTO.setThumbnailUrl(existingProduct.getThumbnail().getUrlImg());
                    productDTO.setImageUrls(existingProduct.getImages().stream()
                            .map(ProductImg::getUrlImg)
                            .collect(Collectors.toList()));
                }
            }

            productService.saveProduct(productDTO);
            return ResponseEntity.ok("Product updated successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: Unable to update product.");
        }
    }
    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }
    @PatchMapping("/{id}/{active}")
    public ResponseEntity<Product> updateProductActiveStatus(@PathVariable Long id, @PathVariable boolean active) {
        Product product = productService.getProductById(id);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }

        product.setActive(active);
        productService.saveProductEntity(product);
        return ResponseEntity.ok(product);
    }

}
