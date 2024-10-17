package app;

import dao.OrderProcessor;
import entity.*;
import JDBC.DBConn;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        try (Connection connection = DBConn.getConnection();
             Scanner scanner = new Scanner(System.in)) {

            OrderProcessor orderProcessor = new OrderProcessor(connection);

            // making a dynamic interface for the user to interact with the program easily!
            while (true) {
                System.out.println("Select an Option : ");
                System.out.println("Select a number : ");

                System.out.println("1 : Create The User");
                System.out.println("2 : Create a Product, Admins only");
                System.out.println("3 : Place Order");
                System.out.println("4 : View All Products");
                System.out.println("5 : View User Orders");
                System.out.println("6 : Cancel a Order");
                System.out.println("7 : Exit The Program");

                int choice = scanner.nextInt();
                scanner.nextLine();

                // using switch and not if for ease!
                switch (choice) {
                    case 1:
                        System.out.println("Enter Your User ID:");
                        int userId = scanner.nextInt();
                        System.out.println("Enter Your Username:");
                        String username = scanner.nextLine();
                        System.out.println("Enter Your Password:");
                        String password = scanner.nextLine();
                        System.out.println("Enter Your Role : User or Admin :");
                        String role = scanner.nextLine();
                        User user = new User(userId, username, password, role);
                        orderProcessor.createUser(user);
                        break;

                    // here as mentioned, only the admin can make a project
                    case 2:
                        System.out.println("Enter the Admin's ID:");
                        int adminId = scanner.nextInt();
                        User admin = new User(adminId, "admin", "adminpass", "Admin");
                        System.out.println("Enter the Product ID:");
                        int productId = scanner.nextInt();
                        scanner.nextLine();
                        System.out.println("Enter the Product Name:");
                        String productName = scanner.nextLine();
                        System.out.println("Enter the Product Description:");
                        String description = scanner.nextLine();
                        System.out.println("Enter the Price:");
                        double price = scanner.nextDouble();
                        System.out.println("Enter the Quantity of the Stock:");
                        int quantity = scanner.nextInt();
                        scanner.nextLine();
                        System.out.println("Enter Product Type : Electronics or Clothing:");
                        String type = scanner.nextLine();

                        Product product;
                        if ("Electronics".equalsIgnoreCase(type)) {

                            System.out.println("Enter the Brand:");
                            String brand = scanner.nextLine();
                            System.out.println("Enter the Warranty Period in months:");
                            int warranty = scanner.nextInt();

                            product = new Electronics(productId, productName, description, price, quantity, brand, warranty);
                        } else if ("Clothing".equalsIgnoreCase(type)) {

                            System.out.println("Enter the size of the cloth:");
                            String size = scanner.nextLine();
                            System.out.println("Enter the color of the cloth:");
                            String color = scanner.nextLine();

                            product = new Clothing(productId, productName, description, price, quantity, size, color);
                        } else {

                            System.out.println("Invalid product type, Try again please");
                            break;
                        }

                        orderProcessor.createProduct(admin, product);
                        break;

                    case 3:
                        System.out.println("Enter the User ID:");
                        userId = scanner.nextInt();
                        User orderUser = new User(userId, "user", "password", "User");
                        System.out.println("Enter the Product ID to order:");
                        productId = scanner.nextInt();
                        Product orderedProduct = orderProcessor.getAllProducts().stream()
                                .filter(p -> p.getProductId() == productId)
                                .findFirst()
                                .orElse(null);

                        if (orderedProduct == null) {

                            System.out.println("Product not found.");
                            break;
                        }
                        List<Product> products = new ArrayList<>();
                        products.add(orderedProduct);
                        orderProcessor.createOrder(orderUser, products);
                        break;

                    case 4:
                        List<Product> allProducts = orderProcessor.getAllProducts();
                        for (Product p : allProducts) {

                            System.out.println("Product ID: " + p.getProductId() + ", Name: " + p.getProductName() + ", Type: " + p.getType());
                        }
                        break;

                    case 5:
                        System.out.println("Enter User ID:");
                        userId = scanner.nextInt();
                        scanner.nextLine();

                        User viewUser = new User(userId, "user", "password", "User");
                        List<Product> userOrders = orderProcessor.getOrderByUser(viewUser);
                        for (Product orderedProductView : userOrders) {

                            System.out.println("Ordered Product: " + orderedProductView.getProductName());
                        }
                        break;

                    case 6:
                        System.out.println("Enter User ID:");
                        userId = scanner.nextInt();

                        System.out.println("Enter Order ID for the orde which needs to be cancled !");
                        int orderId = scanner.nextInt();

                        orderProcessor.cancelOrder(userId, orderId);
                        break;

                    case 7:
                        System.out.println("Exiting The Program...");
                        return;

                    default:
                        System.out.println("Invalid Choice, Select Again Please !");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
