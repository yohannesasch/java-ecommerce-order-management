package com.yohannes.ecommerce.console;

import com.yohannes.ecommerce.reporting.ReportingService;

import java.util.Scanner;

public class ReportsConsole {

    private final ReportingService reportingService;
    private final Scanner scanner;

    public ReportsConsole(ReportingService reportingService, Scanner scanner) {
        this.reportingService = reportingService;
        this.scanner =scanner;
    }

    public void run() {

        boolean runnning = true;

        while (runnning) {

            displayMenu();

            int choice = readInteger("Choose an option: ");

            try {
                switch (choice) {
                    case 1 -> displayTotalRevenue();
                    case 2 -> displayTotalOrders();
                    case 3 -> displayOrdersByStatus();
                    case 4 -> displayBestSellingProducts();
                    case 5 -> displayProductRevenue();
                    case 6 -> displayTopCustomers();
                    case 7 -> displayLowStockProducts();
                    case 8 -> displayCustomerOrderStatistics();
                    case 0 -> runnning = false;
                    default -> System.out.println("Invalid Option.");
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Report failed: " + e.getMessage());
            }
            System.out.println();
        }
    }

    private void displayMenu() {
        System.out.println("""
                ----------------- REPORTS & ANALYTICS -----------------
                  1, Total Revenue
                  2, Total Orders
                  3, Orders by Status
                  4, Best-Selling Products
                  5, Product Revenue
                  6, Top Customers
                  7, Low-Stock Products
                  8, Customer Order Statistics
                  0, Back to Main Menu
                ------------------------------------------------------
                """);
    }

    private void displayTotalRevenue() {
        System.out.println(
                "Total Revenue: " + reportingService.calculateTotalRevenue()
        );
    }

    private void displayTotalOrders() {
        System.out.println(
                "Total Orders: " + reportingService.countTotalOrders()
        );
    }

    private void displayOrdersByStatus() {

        var reports = reportingService.getOrdersByStatus();

        reports.forEach(report ->
                System.out.println(
                        "Status: " + report.status()
                        + " | Orders: " + report.orderCount()
                ));
    }

    private void displayBestSellingProducts() {

        var reports = reportingService.getBestSellingProducts();

        if (reports.isEmpty()) {
            System.out.println("No product sales data available.");
            return;
        }

        reports.forEach(report ->
                System.out.println(
                        "Product: " + report.productName()
                        + " | Quantity Sold: " + report.quantitySold()
                        + " | Revenue: " + report.totalRevenue()
                ));
    }

    private void displayProductRevenue() {

        var reports = reportingService.getRevenueByProduct();

        if (reports.isEmpty()) {
            System.out.println("No product revenue data available.");
            return;
        }

        reports.forEach(report ->
                System.out.println(
                        "Product: " + report.productName() + " | Revenue: " + report.totalRevenue()
                ));
    }

    private void displayTopCustomers() {

        var reports = reportingService.getTopCustomersBySpending();

        if (reports.isEmpty()) {
            System.out.println("No customer spending data available.");
            return;
        }

        reports.forEach(report ->
                System.out.println("Customer: " + report.customerName()
                        + " | Orders: " + report.totalOrders()
                        + " | Total Spent: " + report.totalspent()
                )
        );
    }

    private void displayLowStockProducts() {

        int threshold = readInteger("Enter stock threshold: ");

        var reports = reportingService.getLowStockProducts(threshold);

        if (reports.isEmpty()) {
            System.out.println("No low-stock products found.");
            return;
        }

        reports.forEach(product ->
                System.out.println(
                        "Product: " + product.getName()
                        + " | Stock: " + product.getStockQuantity()
                )
        );
    }

    private void displayCustomerOrderStatistics() {

        var reports = reportingService.getCustomerOrderStatistics();

        if (reports.isEmpty()) {
            System.out.println("No customer statistics available.");
            return;
        }

        reports.forEach( report ->
                System.out.println(
                        "Customer: " + report.customerName()
                        + " | Total Orders: " + report.totalOrders()
                        + " | Delivered: " + report.deliveredOrders()
                        + " | Cancelled: " + report.cancelledOrders()
                )
        );
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
