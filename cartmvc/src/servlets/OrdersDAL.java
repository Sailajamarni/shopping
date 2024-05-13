package servlets;

import java.sql.*;

public class OrdersDAL {

    private String url;
    private String username;
    private String password;
    static int ord_id;

    public OrdersDAL() {
        url = DatabaseConnection.getUrl();
        username = DatabaseConnection.getUsername();
        password = DatabaseConnection.getPassword();
    }

    public boolean insertOrder(double totalCost, int customerId) {
        String sql = "INSERT INTO orders_1(o_total,cust_id) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setDouble(1, totalCost);
            pstmt.setInt(2, customerId);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    ord_id = generatedKeys.getInt(1);
                }
                if (generatedKeys != null) {
                    generatedKeys.close();
                }
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public static int getOrderId() {
        return ord_id;
    }
}
