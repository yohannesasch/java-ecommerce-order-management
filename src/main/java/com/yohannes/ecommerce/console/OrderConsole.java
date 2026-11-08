package com.yohannes.ecommerce.console;

import com.yohannes.ecommerce.domain.Order;
import com.yohannes.ecommerce.service.OrderService;

import java.util.Scanner;

public class OrderConsole {

    private final OrderService orderService;
    private final Scanner scanner;

    public OrderConsole(OrderService orderService, Scanner scanner) {
        this.orderService = orderService;
        this.scanner =scanner;
    }

    public void run() {

        boolean runnning = true;

            while (runnning) {

                displayMenu();
                int choice = readInteger("Choose and option: ");

                try {
                    switch (choice) {
                        case 1 -> placeOrder();
                        case 2 -> findOrder();
                        case 3 -> displayCustomerOrders();
                        case 4 -> confirmOrder();
                        case 5 -> shipOrder();
                        case 6 -> deliverOrder();
                        case 7 -> cancelOrder();
                        case 0 -> runnning = false;
                        default -> System.out.println("Invalid option.");
                    }
                } catch (IllegalArgumentException | IllegalStateException e) {
                    System.out.println("Business Error: " + e.getMessage());
                }
                System.out.println();
            }

    }

    private void displayMenu() {
        System.out.println("""
                -------------- ORDER MANAGEMENT --------------
                  1, Place Order
                  2, Find Order By ID
                  3, View Customer Orders
                  4, Confirm Order
                  5, Ship Order
                  6, Deliver Order
                  7, Cancel Order
                  0, Back to Main Menu
                ----------------------------------------------
                """
        );
    }

    private void placeOrder() {

        String customerId = readString("Customer ID: ");

        Order order = orderService.placeOrder(customerId);

        System.out.println("Order placed successfully!");

        displayOrder(order);
    }

    private void findOrder() {

        String orderId = readString("Order ID: ");

        Order order = orderService.findOrderById(orderId);
        displayOrder(order);

    }

    private void displayCustomerOrders() {

        String customerId = readString("Customer ID: ");

        var orders = orderService.getCustomerOrders(customerId);

        if (orders.isEmpty()) {
            System.out.println("No orders found for this customer.");
            return;
        }

        orders.forEach(this::displayOrder);
    }

    private void confirmOrder() {

        String orderId = readString("Order ID: ");
        orderService.confirmOrder(orderId);
        System.out.println("Order confirmed successfully.");
    }

    private void shipOrder() {

        String orderId = readString("Order ID: ");
        orderService.shipOrder(orderId);
        System.out.println("Order shipped successfully.");
    }

    private void deliverOrder() {

        String orderId = readString("Order ID: ");
        orderService.deliverOrder(orderId);
        System.out.println("Order delivered successfully.");
    }

    private void cancelOrder() {

        String orderId = readString("Order ID: ");
        orderService.cancelOrder(orderId);
        System.out.println("Order cancelled successfully.");
    }

    private void displayOrder(Order order) {

        System.out.println("----------------- ORDER DETAILS -----------------");
        System.out.println("Order ID: " + order.getItems());
        System.out.println("Customer ID: " + order.getCustomerId());
        System.out.println("Status: " + order.getStatus());
        System.out.println("Created At: " + order.getCreatedAt());

        order.getItems().forEach(item -> {
            System.out.println("    Product: " + item.getProductName());
            System.out.println("    Product ID: " + item.getProductId());
            System.out.println("    Unit Price: " + item.getUnitPrice());
            System.out.println("    Quantity: " + item.getQuantity());
            System.out.println("    Subtotal: " + item.getSubtotal());
        });

        System.out.println("Total: " + order.getTotalAmount());

        System.out.println("-------------------------------------------------");
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
