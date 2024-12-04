package com.project.apple.service;

import com.project.apple.dto.ProductDTO;
import com.project.apple.model.Category;
import com.project.apple.model.Product;
import com.project.apple.model.ProductImg;
import com.project.apple.responsitory.CategoryRepository;
import com.project.apple.responsitory.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    public Product saveProduct(ProductDTO productDTO) {
        Product product;
        if (productDTO.getId() != null) {
            product = productRepository.findById(productDTO.getId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));
        } else {
            product = new Product();
        }

        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());

        Category category = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        product.setCategory(category);

        // Initialize the images list if it's null
        if (product.getImages() == null) {
            product.setImages(new ArrayList<>());
        }

        // Only update images if new images are provided
        if (productDTO.getThumbnailUrl() != null || (productDTO.getImageUrls() != null && !productDTO.getImageUrls().isEmpty())) {
            // Clear existing images only if new images are provided
            if (productDTO.getThumbnailUrl() != null) {
                product.setThumbnail(null);
            }
            product.getImages().clear();

            // Add new images
            if (productDTO.getThumbnailUrl() != null) {
                ProductImg thumbnail = new ProductImg();
                thumbnail.setUrlImg(productDTO.getThumbnailUrl());
                thumbnail.setProduct(product);
                product.setThumbnail(thumbnail);
            }

            if (productDTO.getImageUrls() != null) {
                List<ProductImg> images = productDTO.getImageUrls().stream().map(url -> {
                    ProductImg img = new ProductImg();
                    img.setUrlImg(url);
                    img.setProduct(product);
                    return img;
                }).collect(Collectors.toList());
                product.getImages().addAll(images);
            }
        }

        return productRepository.save(product);
    }

    public boolean isProductNameExists(String name) {
        return productRepository.existsByName(name);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
    public Product saveProductEntity(Product product) {
        return productRepository.save(product);
    }
}