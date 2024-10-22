package com.project.apple.controller;

import com.project.apple.model.ProductDetails;
import com.project.apple.service.ProductDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/product-details")
public class ProductDetailsController {

    @Autowired
    private ProductDetailsService productDetailsService;

    @GetMapping
    public List<ProductDetails> getAllProductDetails() {
        return productDetailsService.getAllProductDetails();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDetails> getProductDetailsById(@PathVariable Long id) {
        return productDetailsService.getProductDetailsById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ProductDetails createProductDetails(@RequestBody ProductDetails productDetails) {
        return productDetailsService.createProductDetails(productDetails);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDetails> updateProductDetails(@PathVariable Long id, @RequestBody ProductDetails productDetailsDetails) {
        return ResponseEntity.ok(productDetailsService.updateProductDetails(id, productDetailsDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductDetails(@PathVariable Long id) {
        productDetailsService.deleteProductDetails(id);
        return ResponseEntity.noContent().build();
    }
}