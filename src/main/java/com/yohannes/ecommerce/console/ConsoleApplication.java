package com.yohannes.ecommerce.console;

import java.util.Scanner;

public class ConsoleApplication {

    private final Scanner scanner;
    private final ProductConsole productConsole;
    private final CustomerConsole customerConsole;
    private final CartConsole cartConsole;
    private final OrderConsole orderConsole;
    private final ReportsConsole reportsConsole;

    public ConsoleApplication(
            Scanner scanner,
            ProductConsole productConsole,
            CustomerConsole customerConsole,
            CartConsole cartConsole,
            OrderConsole orderConsole,
            ReportsConsole reportsConsole) {
        this.scanner = scanner;
        this.productConsole = productConsole;
        this.customerConsole = customerConsole;
        this.cartConsole = cartConsole;
        this.orderConsole = orderConsole;
        this.reportsConsole = reportsConsole;
    }


    public void run() {

        boolean running = true;

        while (running) {

            displayMainMenu();
            System.out.print("Choose and option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> productConsole.run();
                case "2" -> customerConsole.run();
                case "3" -> cartConsole.run();
                case "4" -> orderConsole.run();
                case "5" -> reportsConsole.run();
                case "0" -> {
                    running = false;
                    System.out.println("Thank you for using this system.");
                }
                default -> System.out.println("Invalid option. please try again.");
            }
        }
    }



    private void displayMainMenu() {
        System.out.println("""
                -----------------------------------------------------
                |         E-COMMERCE ORDER MANAGEMENT               |
                -----------------------------------------------------
                |  1. Product Management                            |
                |  2, Customer Management                           |
                |  3, Shopping Cart                                 |
                |  4, Order Management                              |
                |  5, Reports                                       |
                |  0, Exit                                          |
                -----------------------------------------------------
                """);
    }
}
