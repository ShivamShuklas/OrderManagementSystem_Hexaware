package JDBC;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConn {

    private static final String URL = "jdbc:mysql://localhost:3306/order_management_system";
    private static final String USER = "root";
    private static final String PASSWORD = "shivamlovespizza";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}