package servlets;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ProductStocksDAL {

    private Connection connection;

    public ProductStocksDAL(Connection connection) {
        this.connection = connection;
    }

    public int getStockQuantityForProduct(int productId) {
        int stockQuantity = 0;
        String query = "SELECT prod_stock FROM ProductStocks_1 WHERE prod_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, productId);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                stockQuantity = rs.getInt("prod_stock");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stockQuantity;
    }

    public void updateStockQuantity(int productId, int newStockQuantity) {
        String query = "UPDATE ProductStocks_1 SET prod_stock = ? WHERE prod_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, newStockQuantity);
            statement.setInt(2, productId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateStockQuantities(List<CartItem> cartItems) {
        for (CartItem item : cartItems) {
            int productId = item.getPid();
            int newStockQuantity = getStockQuantityForProduct(productId) - item.getQuantity();
            updateStockQuantity(productId, newStockQuantity);
        }
    }
}
