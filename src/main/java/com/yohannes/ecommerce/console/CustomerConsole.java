package com.yohannes.ecommerce.console;

import com.yohannes.ecommerce.domain.Customer;
import com.yohannes.ecommerce.service.CustomerService;

import java.util.Scanner;

public class CustomerConsole {

    private final CustomerService customerService;
    private final Scanner scanner;

    public CustomerConsole(CustomerService customerService, Scanner scanner) {
        this.customerService = customerService;
        this.scanner = scanner;
    }

    public void run() {

        boolean running = true;

        while (running) {

            displayMenu();

            int choice = readInteger("Choose and option: ");

            try {
                switch (choice) {
                    case 1 -> registerCustomer();
                    case 2 -> displayAllCustomers();
                    case 3 -> findCustomer();
                    case 4 -> updateCustomerName();
                    case 5 -> updateCustomerEmail();
                    case 6 -> updateCustomerStatus();
                    case 0 -> running = false;
                    default -> System.out.println("Invalid option.");
                }
            } catch (IllegalStateException e) {
                System.out.println("Operation failed: " + e.getMessage());
            }
            System.out.println();
        }
    }


    private void displayMenu() {
        System.out.println("""
                -------------- CUSTOMER MANAGEMENT ---------------
                  1, Register Customer
                  2, view All Customers
                  3, Find Customer by ID
                  4, Update Customer Name
                  5, Update Customer Email
                  6, Active/Deactive Customer
                  0, Back to Main Menu
                --------------------------------------------------
                """);
    }

    private void registerCustomer() {
        String id = readString("Customer ID: ");
        String name = readString("Customer name: ");
        String email = readString("Customer email: ");

        Customer customer = new Customer(id, name, email, true);

        customerService.registerCustomer(customer);

        System.out.println("Customer registered successfully.");
    }

    private void displayAllCustomers() {

        var customers = customerService.getAllCustomers();

        if (customers.isEmpty()) {
            System.out.println("No customers found.");
            return;
        }

        customers.forEach(System.out::println);
    }

    private void findCustomer() {

        String customerId = readString("Enter customer ID: ");

        customerService.findCustomerById(customerId)
                .ifPresentOrElse(
                        System.out::println,
                        () -> System.out.println("Customer not found.")
                );
    }

    private void updateCustomerName() {

        String customerId = readString("Customer ID: ");
        String newName = readString("New name: ");

        customerService.updateCustomerName(customerId, newName);

        System.out.println("Customer name updated successfully.");
    }

    private void updateCustomerEmail() {

        String customerId = readString("Customer ID: ");
        String newEmail = readString("New email: ");

        customerService.updateCustomerEmail(customerId, newEmail);

        System.out.println("Customer email updated successfully.");
    }

    private void updateCustomerStatus() {

        String customerId = readString("Customer ID: ");

        String answer = readString("Active Customer? (yes/no): ");

        switch (answer.toLowerCase()) {
            case "yes" -> customerService.activateCustomer(customerId);
            case "no" -> customerService.deactivateCustomer(customerId);
            default -> throw new IllegalArgumentException(
                    "Please enter yes or no."
            );
        };

        System.out.println("Customer status updated successfully.");
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


