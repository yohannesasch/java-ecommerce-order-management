package com.yohannes.ecommerce;

import com.yohannes.ecommerce.console.*;
import com.yohannes.ecommerce.reporting.ReportingService;
import com.yohannes.ecommerce.repository.impl.InMemoryCartRepository;
import com.yohannes.ecommerce.repository.impl.InMemoryOrderRepository;
import com.yohannes.ecommerce.repository.impl.InMemoryProductRepository;
import com.yohannes.ecommerce.service.CartService;
import com.yohannes.ecommerce.service.CustomerService;
import com.yohannes.ecommerce.service.OrderService;
import com.yohannes.ecommerce.service.ProductService;
import com.yohannes.ecommerce.repository.impl.InMemoryCustomerRepository;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);



        InMemoryProductRepository productRepository =
                new InMemoryProductRepository();

        ProductService productService =
                new ProductService(productRepository);

        ProductConsole productConsole =
                new ProductConsole(productService, scanner);




        InMemoryCustomerRepository customerRepository =
                new InMemoryCustomerRepository();

        CustomerService customerService =
                new CustomerService(customerRepository);

        CustomerConsole customerConsole =
                new CustomerConsole(customerService, scanner);





        InMemoryCartRepository cartRepository =
                new InMemoryCartRepository();

        CartService cartService =
                new CartService(
                        cartRepository,
                        customerRepository,
                        productRepository
                );

        CartConsole cartConsole =
                new CartConsole(cartService, scanner);






        InMemoryOrderRepository orderRepository =
                new InMemoryOrderRepository();

        OrderService orderService =
                new OrderService(
                        orderRepository,
                        productRepository,
                        customerRepository,
                        cartRepository
                );
        OrderConsole orderConsole =
                new OrderConsole(orderService, scanner);


        ReportingService reportingService =
                new ReportingService(
                        orderRepository,
                        customerRepository,
                        productRepository
                );

        ReportsConsole reportsConsole =
                new ReportsConsole(reportingService, scanner);





        ConsoleApplication application =
                new ConsoleApplication(
                        scanner,
                        productConsole,
                        customerConsole,
                        cartConsole,
                        orderConsole,
                        reportsConsole);

        application.run();



    }
}