package com.project.apple.service;

import com.project.apple.model.ProductDetails;
import com.project.apple.responsitory.ProductDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductDetailsService {

    @Autowired
    private ProductDetailsRepository productDetailsRepository;

    public List<ProductDetails> getAllProductDetails() {
        return productDetailsRepository.findAll();
    }

    public Optional<ProductDetails> getProductDetailsById(Long id) {
        return productDetailsRepository.findById(id);
    }

    public ProductDetails createProductDetails(ProductDetails productDetails) {
        return productDetailsRepository.save(productDetails);
    }

    public ProductDetails updateProductDetails(Long id, ProductDetails productDetailsDetails) {
        ProductDetails productDetails = productDetailsRepository.findById(id).orElseThrow();
        productDetails.setProduct(productDetailsDetails.getProduct());
        productDetails.setSize(productDetailsDetails.getSize());
        productDetails.setColor(productDetailsDetails.getColor());
        productDetails.setInStock(productDetailsDetails.getInStock());
        return productDetailsRepository.save(productDetails);
    }

    public void deleteProductDetails(Long id) {
        productDetailsRepository.deleteById(id);
    }
}