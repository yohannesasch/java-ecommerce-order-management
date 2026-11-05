package com.yohannes.ecommerce.service;


import com.yohannes.ecommerce.domain.Product;
import com.yohannes.ecommerce.domain.ProductCategory;
import com.yohannes.ecommerce.repository.impl.InMemoryProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class ProductServiceTest {

    private ProductService productService;
    private Product product;

    @BeforeEach
    void setUp() {
        InMemoryProductRepository repository =
                new InMemoryProductRepository();
        productService = new ProductService(repository);

        product = new Product(
                "P001",
                "Laptop",
                "Dell laptop",
                ProductCategory.ELECTRONICS,
                new BigDecimal("50000.00"),
                10,
                true
        );
    }

    @Test
    void shouldCeateProduct() {
        productService.createProduct(product);

        Product found = productService
                .findProductById("P001")
                .orElseThrow();

        assertEquals("Laptop", found.getName());
    }

    @Test
    void shouldRejetDuplicateProductId() {
        productService.createProduct(product);

        assertThrows(
                IllegalArgumentException.class,
                () -> productService.createProduct(product)
        );
    }

    @Test
    void shouldUpdateProductPrice() {
        productService.createProduct(product);

        productService.updateProductPrice(
                "P001",
                new BigDecimal("55000.00")
        );

        Product updated = productService
                .findProductById("P001")
                .orElseThrow();

        assertEquals(
                new BigDecimal("55000.00"),
                updated.getPrice()
        );
    }

    @Test
    void shouldUpdateProductStock() {
        productService.createProduct(product);

        productService.updateProductStock("P001", 25);

        Product updated = productService
                .findProductById("P001")
                .orElseThrow();

        assertEquals(25, updated.getStockQuantity());
    }

    @Test
    void shouldDeactivateProduct() {
        productService.createProduct(product);

        productService.updateProductStatus("P001", false);

        Product updated = productService
                .findProductById("P001")
                .orElseThrow();

        assertFalse(updated.isActive());
    }

    @Test
    void shouldRejectUpdatingNonexistentProduct() {
        assertThrows(
                IllegalArgumentException.class,
                () -> productService.updateProductPrice(
                        "P999",
                        new BigDecimal("1000.00")
                        )
        );
    }

    @Test
    void shouldReturnAllProducts() {
        productService.createProduct(product);

        assertEquals(1, productService.getAllProducts().size());
    }
}
