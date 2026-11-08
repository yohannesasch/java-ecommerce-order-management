package com.yohannes.ecommerce.service;

import com.yohannes.ecommerce.domain.Product;
import com.yohannes.ecommerce.exception.DuplicateResourceException;
import com.yohannes.ecommerce.exception.ResourceNotFoundException;
import com.yohannes.ecommerce.repository.ProductRepository;
import com.yohannes.ecommerce.validation.ValidationUtils;

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
        ValidationUtils.requireNotBlank(productId, "Product ID");

        return productRepository.findById(productId);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public void createProduct(Product product) {
        ValidationUtils.requireNotNull(product, "Product");

        if (productRepository.existsById(product.getId())){
            throw new DuplicateResourceException(
                    "Product already exists: " + product.getId()
            );
        }

        productRepository.save(product);
    }

    public void updateProductPrice(String productId, BigDecimal newPrice) {

        ValidationUtils.requireNotBlank(productId, "Product ID");
        ValidationUtils.requirePositive(newPrice, "Product price");

        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found: " + productId
                ));

        Product updatedProduct = existingProduct.withPrice(newPrice);

        productRepository.save(updatedProduct);
    }

    public void updateProductStock(String productId, int newStockQuantity) {

        ValidationUtils.requireNotBlank(productId, "Product ID");
        ValidationUtils.requireNonNegative(newStockQuantity, "Stock quantity");

        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Product not found: " + productId
                ));

        Product updatedProduct =
                existingProduct.withStockQuantity(newStockQuantity);

        productRepository.save(updatedProduct);
    }

    public void updateProductStatus(String productId, boolean active) {

        ValidationUtils.requireNotBlank(productId, "product ID");

        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Product not found: " + productId
                ));

        Product updatedProduct = existingProduct.withActive(active);

        productRepository.save(updatedProduct);
    }
}
