package dao;

import entity.Product;
import entity.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderProcessor implements IOrderManagementRepository {

    private Connection connection;

    // making an object here for ease so that we can use it whereever we need it. !
    public OrderProcessor(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void createOrder(User user, List<Product> products) {
        try {

            /*
             this method first checks if the user exits or not then only it creates a user and,
              then creates the order or that specific user
            */

            String checkUserQuery = "select * from users where userId = ?";
            PreparedStatement checkUserStmt = connection.prepareStatement(checkUserQuery);
            checkUserStmt.setInt(1, user.getUserId());
            ResultSet rs = checkUserStmt.executeQuery();

            if (!rs.next()) { // this is the pointer.

                createUser(user);
            }

            String createOrderQuery = "insert into orders (userId) values (?)";
            PreparedStatement createOrderStmt = connection.prepareStatement(createOrderQuery, Statement.RETURN_GENERATED_KEYS);
            createOrderStmt.setInt(1, user.getUserId());
            createOrderStmt.executeUpdate();
            ResultSet generatedKeys = createOrderStmt.getGeneratedKeys();
            int orderId = 0;
            if (generatedKeys.next()) { // this is the pointer as well !

                orderId = generatedKeys.getInt(1);
            }

            String addProductToOrderQuery = "insert into order_items (orderId, productId, quantity) "+
                                                "values (?, ?, ?)";
            PreparedStatement addProductToOrderStmt = connection.prepareStatement(addProductToOrderQuery);
            for (Product product : products) {

                addProductToOrderStmt.setInt(1, orderId);
                addProductToOrderStmt.setInt(2, product.getProductId());
                addProductToOrderStmt.setInt(3, 1);
                addProductToOrderStmt.executeUpdate();
            }

            System.out.println("Order created successfully with Order ID: " + orderId);
        }

        catch (SQLException ex) {

            ex.printStackTrace();
            System.out.println(ex.getMessage()); // to get the message in detail on the console !
        }
    }

    @Override
    public void cancelOrder(int userId, int orderId) {
        try {

            String checkOrderQuery = "select * from orders where orderId = ? and userId = ?";
            PreparedStatement checkOrderStmt = connection.prepareStatement(checkOrderQuery);
            checkOrderStmt.setInt(1, orderId);
            checkOrderStmt.setInt(2, userId);
            ResultSet rs = checkOrderStmt.executeQuery();

            if (!rs.next()) {
                throw new SQLException("Order not found for user ID: " + userId + " and order ID: " + orderId);
            }


            String cancelOrderQuery = "delete from orders where orderId = ?";
            PreparedStatement cancelOrderStmt = connection.prepareStatement(cancelOrderQuery);
            cancelOrderStmt.setInt(1, orderId);
            cancelOrderStmt.executeUpdate();

            System.out.println("Order canceled successfully for Order ID: " + orderId);
        }

        catch (SQLException ex) {

            ex.printStackTrace();
            System.out.println(ex.getMessage()); // to get the message in detail on the console !
        }
    }

    @Override
    public void createProduct(User user, Product product) {
        try {

            /*
            first checking the user should be an admin, otherwise not be able to make the product !
             */
            if (!"Admin".equals(user.getRole())) {

                throw new SQLException("Only admins can create any products.");
            }

            String createProductQuery = "insert into products (productName, description, price, quantityInStock, type)"
                                            +" values (?, ?, ?, ?, ?)";
            PreparedStatement createProductStmt = connection.prepareStatement(createProductQuery, Statement.RETURN_GENERATED_KEYS);
            createProductStmt.setString(1, product.getProductName());
            createProductStmt.setString(2, product.getDescription());
            createProductStmt.setDouble(3, product.getPrice());
            createProductStmt.setInt(4, product.getQuantityInStock());
            createProductStmt.setString(5, product.getType());
            createProductStmt.executeUpdate();

            ResultSet generatedKeys = createProductStmt.getGeneratedKeys();
            int productId = 0;

            if (generatedKeys.next()) {

                productId = generatedKeys.getInt(1);
            }

            if ("Electronics".equals(product.getType())) {

                String createElectronicsQuery = "insert into electronics (productId, brand, warrantyPeriod)"+
                                    " values (?, ?, ?)";
                PreparedStatement createElectronicsStmt = connection.prepareStatement(createElectronicsQuery);
                createElectronicsStmt.setInt(1, productId);
                createElectronicsStmt.setString(2, ((entity.Electronics) product).getBrand());
                createElectronicsStmt.setInt(3, ((entity.Electronics) product).getWarrantyPeriod());

                createElectronicsStmt.executeUpdate();

            } else if ("Clothing".equals(product.getType())) {

                String createClothingQuery = "insert into clothing (productId, size, color) "+
                        "values (?, ?, ?)";

                PreparedStatement createClothingStmt = connection.prepareStatement(createClothingQuery);
                createClothingStmt.setInt(1, productId);
                createClothingStmt.setString(2, ((entity.Clothing) product).getSize());
                createClothingStmt.setString(3, ((entity.Clothing) product).getColor());

                createClothingStmt.executeUpdate();
            }

            System.out.println("Product created successfully with Product ID: " + productId);
        }
        catch (SQLException ex) {

            ex.printStackTrace();
            System.out.println(ex.getMessage()); // to get the message in detail on the console !
        }
    }

    @Override
    public void createUser(User user) {
        try {

            String createUserQuery = "insert into users (username, password, role) values (?, ?, ?)";
            PreparedStatement createUserStmt = connection.prepareStatement(createUserQuery);
            createUserStmt.setString(1, user.getUsername());
            createUserStmt.setString(2, user.getPassword());
            createUserStmt.setString(3, user.getRole());
            createUserStmt.executeUpdate();

            System.out.println("User created successfully with User ID: " + user.getUserId());
        }
        catch (SQLException ex) {

            ex.printStackTrace();
            System.out.println(ex.getMessage());
        }
    }

    @Override
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        try {

            String getAllProductsQuery = "select * from products";
            PreparedStatement getAllProductsStmt = connection.prepareStatement(getAllProductsQuery);
            ResultSet rs = getAllProductsStmt.executeQuery();

            while (rs.next()) {
                Product product;
                if ("Electronics".equals(rs.getString("type"))) {

                    product = new entity.Electronics(rs.getInt("productId"), rs.getString("productName"),
                            rs.getString("description"), rs.getDouble("price"), rs.getInt("quantityInStock"),
                            "Electronics", 0);
                }
                else {

                    product = new entity.Clothing(rs.getInt("productId"), rs.getString("productName"),
                            rs.getString("description"), rs.getDouble("price"), rs.getInt("quantityInStock"),
                            "M", "Blue");
                }
                products.add(product);
            }
        }
        catch (SQLException ex) {

            ex.printStackTrace();
            System.out.println(ex.getMessage()); // to get the message in detail on the console !
        }
        return products;
    }

    @Override
    public List<Product> getOrderByUser(User user) {
        List<Product> products = new ArrayList<>();
        try {

            String getOrderByUserQuery = "select p.* from products p join order_items oi on "+
                    " p.productId= oi.productId join orders o on "+
                    " o.orderId = oi.orderId where o.userId = ?";

            PreparedStatement getOrderByUserStmt = connection.prepareStatement(getOrderByUserQuery);
            getOrderByUserStmt.setInt(1, user.getUserId());
            ResultSet rs = getOrderByUserStmt.executeQuery();

            while (rs.next()) {

                Product product;
                if ("Electronics".equals(rs.getString("type"))) {
                    product = new entity.Electronics(rs.getInt("productId"), rs.getString("productName"),
                            rs.getString("description"), rs.getDouble("price"), rs.getInt("quantityInStock"),
                            "Electronics", 0);
                }
                else {
                    product = new entity.Clothing(rs.getInt("productId"), rs.getString("productName"),
                            rs.getString("description"), rs.getDouble("price"), rs.getInt("quantityInStock"),
                            "M", "Blue");
                }
                products.add(product);
            }
        }
        catch (SQLException ex) {

            ex.printStackTrace();
            System.out.println(ex.getMessage()); // to get the message in detail on the console !
        }
        return products;
    }
}