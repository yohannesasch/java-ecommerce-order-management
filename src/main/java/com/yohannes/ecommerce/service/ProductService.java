package com.yohannes.ecommerce.service;

import com.yohannes.ecommerce.domain.Product;
import com.yohannes.ecommerce.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = Objects.requireNonNull(
                productRepository,
                "Product repository cannot be null"
        );
    }

    public Optional<Product> findProductById(String productId) {
        return productRepository.findById(productId);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public void createProduct(Product product) {
        Objects.requireNonNull(product, "Product cannot be null");

        if (productRepository.existsById(product.getId())){
            throw new IllegalArgumentException(
                    "Product already exists: " + product.getId()
            );
        }

        productRepository.save(product);
    }

    public void updateProductPrice(String productId, BigDecimal newPrice) {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Product not found: " + productId
                ));

        Product updatedProduct = existingProduct.withPrice(newPrice);

        productRepository.save(updatedProduct);
    }

    public void updateProductStock(String productId, int newStockQuantity) {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Product not found: " + productId
                ));

        Product updatedProduct =
                existingProduct.withStockQuantity(newStockQuantity);

        productRepository.save(updatedProduct);
    }

    public void updateProductStatus(String productId, boolean active) {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Product not found: " + productId
                ));

        Product updatedProduct = existingProduct.withActive(active);

        productRepository.save(updatedProduct);
    }
}
