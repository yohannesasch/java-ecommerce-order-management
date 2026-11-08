package com.yohannes.ecommerce.console;

import com.yohannes.ecommerce.domain.CartItem;
import com.yohannes.ecommerce.domain.ShoppingCart;
import com.yohannes.ecommerce.service.CartService;

import java.util.Scanner;

public class CartConsole {

    private final CartService cartService;
    private final Scanner scanner;

    public CartConsole(CartService cartService, Scanner scanner) {
        this.cartService = cartService;
        this.scanner = scanner;
    }

    public void run() {

        boolean running = true;

        while (running) {

            displayMenu();

            int choice = readInteger("Choose an Option: ");

            try {
                switch (choice) {
                    case 1 -> viewCart();
                    case 2 -> addProduct();
                    case 3 -> removeProduct();
                    case 4 -> updateProductQuantity();
                    case 5 -> calculateTotal();
                    case 6 -> clearCart();
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
                ------------- SHOPPING CART -------------
                  1, View Cart
                  2, Add Product
                  3, Remove Product
                  4, Update Product Quantity
                  5, Calculate Cart Total
                  6, Clear Cart
                  0, Back to Main Menu
                -----------------------------------------
                """);
    }

    private void viewCart() {

        String customerId = readString("Customer ID: ");

        ShoppingCart cart = cartService.getCart(customerId);

        if (cart.isEmpty()) {
            System.out.println("Your shopping cart is empty.");
            return;
        }

        System.out.println("\n------------ YOUR CART -------------");

        for (CartItem item : cart.getItems()) {
            System.out.println("Product: " + item.getProduct().getName());
            System.out.println("Product ID: " + item.getProduct().getId());
            System.out.println("Unit Price: " + item.getProduct().getPrice());
            System.out.println("Quantity: " + item.getQuantity());
            System.out.println("-------------------------------------");
        }

        System.out.println("Total: " + cartService.calculateTotal(customerId));
    }

    private void addProduct() {

        String customerId = readString("Customer ID: ");
        String productId = readString("Product ID: ");
        int quantity = readInteger("Quantity: ");

        cartService.addProduct(customerId, productId, quantity);

        System.out.println("Product added to cart successfully.");
    }

    private void removeProduct() {

        String customerId = readString("Customer ID: ");
        String productId = readString("Product ID to remove: ");

        cartService.removeProduct(customerId, productId);

        System.out.println("Product removed from cart successfully.");
    }

    private void updateProductQuantity() {

        String customerId = readString("Customer ID: ");
        String productId = readString("Product ID: ");
        int quantity = readInteger("New Quantity: ");

        cartService.updateProductQuantity(customerId, productId, quantity);

        System.out.println("Product quantity updated successfully.");
    }

    private void calculateTotal() {

        String customerId = readString("Customer ID: ");

        System.out.println("Cart total: " + cartService.calculateTotal(customerId));

    }

    private void clearCart() {

        String customerId = readString("Customer ID: ");

        cartService.clearCart(customerId);

        System.out.println("Shopping cart cleared successfully.");
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
}
