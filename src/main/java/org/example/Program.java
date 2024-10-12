package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLOutput;
import java.util.List;
import java.util.Scanner;

/**
 * Hello world!
 *
 */
public class Program {
    /*
    * Step to connect JDBC
    * 1. Create and connect to database
    * 2. Make queries
    * 3. Close connection
    * */

    private static Scanner scanner = new Scanner(System.in);

    public static void main( String[] args ) {
        System.out.print("Enter Server's url: ");    //  jdbc:mysql://localhost:3306
        String url = scanner.nextLine();
        System.out.print("Enter User: ");  // root
        String user = scanner.nextLine();
        System.out.print("Enter Password: "); // root
        String pass = scanner.nextLine();

        ProductDao productDao = new ProductDao(url, user, pass);

        if (productDao.getConnection() != null) {
            System.out.println("connection successfully!!");

            handleUserChoose(productDao);
        } else {
            System.out.println("Connect database failure!");
            System.out.println("----END PROGRAM----");
        }
    }

    private static void handleUserChoose(ProductDao productDao) {
        boolean existOption = false;

        do {
            System.out.println("\n----------Options----------");
            System.out.println("1. Read all products");
            System.out.println("2. Read detail of a product by ID");
            System.out.println("3. Add a new product");
            System.out.println("4. Update a product");
            System.out.println("5. Delete a product");
            System.out.println("6. Exit");
            System.out.println("\nYour choice: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    handleReadAllProducts(productDao);
                    break;
                case "2":
                    handleReadProductById(productDao);
                    break;
                case "3":
                    handleAddNewProduct(productDao);
                    break;
                case "4":
                    handleUpdateProduct(productDao);
                    break;
                case "5":
                    handleDeleteProduct(productDao);
                    break;
                case "6":
                    existOption = true;
                    System.out.println("----END PROGRAM----");
                    break;
                default:
                    System.out.println("Your select is not exist, please choose again");
            }
        } while (!existOption);
    }

    private static void handleReadAllProducts(ProductDao productDao) {
        List<Product> products = productDao.readAll();
        System.out.println("\nProduct list: ");
        products.forEach(System.out::println);
    }

    private static void handleReadProductById(ProductDao productDao) {
        System.out.print("Enter product ID: ");
        Integer productId = scanner.nextInt();
        scanner.nextLine();
        Product product = productDao.read(productId);
        if (product != null) {
            System.out.println("\nProduct details: ");
            System.out.println("Product ID: " + productId);
            System.out.println("Product Name: " + product.getName());
            System.out.println("Product Price: " + product.getPrice());
        } else {
            System.out.println("Product not found!");
        }
    }

    private static void handleAddNewProduct(ProductDao productDao) {
        System.out.println("Enter product name: ");
        String name = scanner.nextLine();
        System.out.println("Enter product price: ");
        Integer price = scanner.nextInt();
        scanner.nextLine();
        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        if (productDao.add(product) != null) {
            System.out.println("add successfully!");
        } else {
            System.out.println("add failed!");
        }
    }

    private static void handleUpdateProduct(ProductDao productDao) {
        System.out.print("Enter product ID: ");
        Integer productId = scanner.nextInt();
        scanner.nextLine();

        if (productDao.read(productId) == null) {
            System.out.println("Product not found!");
        } else {
            System.out.print("Enter product name: ");
            String name = scanner.nextLine();
            System.out.print("Enter product price: ");
            Integer price = scanner.nextInt();
            scanner.nextLine();
            Product product = new Product();
            product.setName(name);
            product.setPrice(price);

            if (productDao.update(productId, product)) {
                System.out.println("update successfully!");
            } else {
                System.out.println("update failed!");
            }
        }


    }

    private static void handleDeleteProduct(ProductDao productDao) {
        System.out.print("Enter product ID: ");
        Integer productId = scanner.nextInt();
        scanner.nextLine();
        if (productDao.delete(productId)) {
            System.out.println("delete successfully!");
        } else {
            System.out.println("delete failed!");
        }
    }
}
