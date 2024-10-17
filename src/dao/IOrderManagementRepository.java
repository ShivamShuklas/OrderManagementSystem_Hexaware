package dao;

import entity.Product;
import entity.User;
import java.util.List;

public interface IOrderManagementRepository {

    void createOrder(User user, List<Product> products);
    void cancelOrder(int userId, int orderId);
    void createProduct(User user, Product product);
    void createUser(User user);
    List<Product> getAllProducts();
    List<Product> getOrderByUser(User user);

}