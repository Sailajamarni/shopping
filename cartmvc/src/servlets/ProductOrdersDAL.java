package servlets;

import java.sql.*;

public class ProductOrdersDAL {

    private String url;
    private String username;
    private String password;

    public ProductOrdersDAL() {
        url = DatabaseConnection.getUrl();
        username = DatabaseConnection.getUsername();
        password = DatabaseConnection.getPassword();
    }

    public boolean insertProductOrder(int orderId, int productId, int quantity, double price) {
        String sql = "INSERT INTO productorder_1(o_id,p_id,quantity,price) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, orderId);
            pstmt.setInt(2, productId);
            pstmt.setInt(3, quantity);
            pstmt.setDouble(4, price);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
