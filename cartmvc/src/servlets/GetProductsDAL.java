package servlets;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GetProductsDAL {

    private String url;
    private String username;
    private String password;
    private String driver;
    private List<Products> productsList;
    private static Connection cn;

    public GetProductsDAL() {
        url = DatabaseConnection.getUrl();
        username = DatabaseConnection.getUsername();
        password = DatabaseConnection.getPassword();
        driver = DatabaseConnection.getDriver();
        productsList = new ArrayList<>();
        try {
            Class.forName(driver);
            cn = DriverManager.getConnection(url, username, password);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public List<Products> getProducts(int categoryId) {
        String query = "SELECT * FROM product_1 WHERE category_id = ?";
        try (PreparedStatement pst = cn.prepareStatement(query)) {
            pst.setInt(1, categoryId);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    int pid = rs.getInt("id");
                    String pname = rs.getString("title");
                    double pprice = rs.getDouble("price");
                    String pimag = rs.getString("image");
                    int hsncode = rs.getInt("hsnid");
                    Products product = new Products(pid, pname, pprice, pimag, categoryId, hsncode);
                    productsList.add(product);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productsList;
    }

    public static Products getProductById(int id) {
        String query = "SELECT * FROM Product_1 WHERE id = ?";
        Products p = null;
        try (PreparedStatement pst = cn.prepareStatement(query)) {
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    int pid = rs.getInt("id");
                    int pcatid = rs.getInt("category_id");
                    String pname = rs.getString("title");
                    double pprice = rs.getDouble("price");
                    String pimag = rs.getString("image");
                    int hsncode = rs.getInt("hsnid");
                    p = new Products(pid, pname, pprice, pimag, pcatid, hsncode);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return p;
    }
    public static double getGSTForProduct(int hsnCode) {
        double gst = 0.0;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            String sql = "SELECT hsnc_gstc_percentage FROM  HSNCodes_1 WHERE hsnc_hsncode= ?";
            pstmt = cn.prepareStatement(sql);
            pstmt.setInt(1, hsnCode);
            rs = pstmt.executeQuery();
            
            // If a record is found, retrieve GST
            if (rs.next()) {
                gst = rs.getDouble("hsnc_gstc_percentage");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close resources
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (cn != null) cn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return gst;
    }
    public double getProductPrice(int productId) throws SQLException {
        double productPrice = 0.0;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            
            String sql = "SELECT price FROM product_1 WHERE id = ?";
            stmt = cn.prepareStatement(sql);
            stmt.setInt(1, productId);
            rs = stmt.executeQuery();
            if (rs.next()) {
                productPrice = rs.getDouble("price");
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (cn != null) {
                cn.close();
            }
        }
        return productPrice;
    }

}
