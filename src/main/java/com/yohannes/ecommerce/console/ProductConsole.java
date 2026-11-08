package com.yohannes.ecommerce.console;

import com.yohannes.ecommerce.domain.Product;
import com.yohannes.ecommerce.domain.ProductCategory;
import com.yohannes.ecommerce.service.ProductService;

import java.math.BigDecimal;
import java.util.Scanner;

public class ProductConsole {

    private final ProductService productService;
    private final Scanner scanner;

    public ProductConsole(ProductService productService, Scanner scanner) {
        this.productService = productService;
        this.scanner = scanner;
    }

    public void run() {

        boolean running = true;

        while (running) {

            displayMenu();

            int choice = readInteger("Choose an option ");

            try {

                switch (choice) {
                    case 1 -> createProduct();
                    case 2 -> displayAllProducts();
                    case 3 -> findProduct();
                    case 4 -> updateProductPrice();
                    case 5 -> updateProductStock();
                    case 6 -> updateProductStatus();
                    case 0 -> running = false;
                    default -> System.out.println("Invalid option.");
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Operation failed: " + e.getMessage());
            }
            System.out.println();
        }
    }

    private void displayMenu() {
        System.out.println("""
                ------------- PRODUCT MANAGEMENT --------------
                 1, Create Product
                 2, View All Products
                 3, Find Product by ID
                 4, Update Product Price
                 5, Update Product stock
                 6, Active/Deactive Product
                 0, Back to Main Menu
                 ----------------------------------------------
                """);
    }

    private void createProduct() {

        String id = readString("Product ID: ");
        String name = readString("Product name: ");
        String description = readString("Description: ");
        ProductCategory category = readCategory();
        BigDecimal price = new BigDecimal(readString("Price: "));
        int stock = readInteger("Initial stock quantity: ");

        Product product = new Product(
                id,
                name,
                description,
                category,
                price,
                stock,
                true
        );

        productService.createProduct(product);

        System.out.println("Product Created successfully.");
    }


    private void displayAllProducts() {

        var products = productService.getAllProducts();

        if (products.isEmpty()) {
            System.out.println("No products found.");
            return;
        }

        products.forEach(System.out::println);
    }


    private void findProduct() {

        String productId = readString("Enter product ID: ");

        productService.findProductById(productId)
                .ifPresentOrElse(
                        System.out::println,
                        () -> System.out.println("Product not found.")
                );

    }

    private void updateProductPrice() {

        String productId = readString("Product ID: ");
        BigDecimal newPrice = new BigDecimal(readString("New price: "));

        productService.updateProductPrice(productId, newPrice);

        System.out.println("Product price updated successfully.");
    }

    private void updateProductStock() {

        String productId = readString("Product ID");
        int newStock = readInteger("New stock quantity: ");

        productService.updateProductStock(productId, newStock);

        System.out.println("Product stock updated successfully.");
    }

    private void updateProductStatus() {

        String productId = readString("Product ID ");

        String answer = readString("Active Product? (yes/no): ");

        boolean active = switch (answer.toLowerCase()) {
            case "yes" -> true;
            case "no" -> false;
            default -> throw new IllegalArgumentException(
                    "Please enter yes or no."
            );
        };

        productService.updateProductStatus(productId, active);

        System.out.println("Product status updated successfully.");
    }



    // Helper methods

    private String readString(String prompt) {
        while (true) {
            System.out.print(prompt);
            return scanner.nextLine().trim();
        }
    }

    private int readInteger(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Please enter a valid integer.");
            }
        }
    }

    private ProductCategory readCategory() {
        System.out.println("Available categories:");

        int threshold = 1;

        System.out.println("-------------------------");
        for (ProductCategory category : ProductCategory.values()) {
            System.out.println("  "  + threshold + ", " + category);
            threshold++;
        }
        System.out.println("--------------------------");
        System.out.print("Select a category number: ");

        ProductCategory[] categories = ProductCategory.values();
        String input = scanner.nextLine().trim();

        return switch (input) {
            case "1" -> categories[0];
            case "2" -> categories[1];
            case "3" -> categories[2];
            case "4" -> categories[3];
            case "5" -> categories[4];

            default -> {
                try {
                    int choice = Integer.parseInt(input);
                    if (choice >= 1 && choice <= categories.length) {
                        yield categories[choice - 1];
                    }
                } catch (NumberFormatException ignored){}

                System.out.println("Invalid selection. Please try again.");
                yield readCategory();
            }
        };
    }
}
