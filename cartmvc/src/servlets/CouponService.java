package servlets;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CouponService {
	private String url;
    private String username;
    private String password;
    private String driver;
    public CouponService() {
    	url = DatabaseConnection.getUrl();
        username = DatabaseConnection.getUsername();
        password = DatabaseConnection.getPassword();
        driver = DatabaseConnection.getDriver();
        try {
            Class.forName(driver);
            cn = DriverManager.getConnection(url, username, password);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
	}

	private static Connection cn;
    
	public double determineDiscount(String couponCode, double orderTotal) {
        double discountAmount = 0.0;

        // Query the database to retrieve discount details based on the coupon code
        try (PreparedStatement statement = cn.prepareStatement("SELECT dcpn_val, dcpn_no, dcpn_minval FROM discountcoupon WHERE dcpn_code = ?")) {
            statement.setString(1, couponCode);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    double discountPercentage = resultSet.getDouble("dcpn_val");
                    int maxDiscountAmount = resultSet.getInt("dcpn_no");
                    double minOrderValue = resultSet.getDouble("dcpn_minval");

                    // Apply discount based on order total and discount rules
                    if (orderTotal >= minOrderValue) {
                        discountAmount = orderTotal * (discountPercentage / 100);
                        // Apply maximum discount limit
                        discountAmount = Math.min(discountAmount, maxDiscountAmount);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle database exception
        }

        return discountAmount;
    }
	
	public boolean applyCouponAndDecrementQuantity(String couponCode) throws SQLException {
        // Retrieve the coupon from the database
        Coupon coupon = getCouponByCode(couponCode);
        if (coupon != null) {
            // Check if there are available coupons
            if (coupon.getAvailableCount() > 0) {
                // Apply the coupon and decrement its quantity
                decrementCouponQuantity(coupon);
                return true; // Coupon applied successfully
            }
        }
        return false; // Coupon not applied (invalid or out of stock)
    }

	private Coupon getCouponByCode(String couponCode) throws SQLException {
		try (PreparedStatement stmt=cn.prepareStatement("SELECT * FROM coupons WHERE dcpn_code = ?")){
			stmt.setString(1, couponCode);
			ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // Create a Coupon object with the retrieved data
                Coupon coupon = new Coupon();
                coupon.setId(rs.getInt("dcpn_id"));
                coupon.setCode(rs.getString("dcpn_code"));
                coupon.setValue(rs.getDouble("dcpn_val"));
                coupon.setAvailableCount(rs.getInt("dcpn_no"));
                coupon.setMinValue(rs.getDouble("dcpn_minval"));
                return coupon;
            }
		}catch (SQLException e) {
            e.printStackTrace();
            // Handle database exception
        }
		return null;
	}

	private boolean isValidCouponCode(String couponCode) {
		// TODO Auto-generated method stub
		return couponCode != null && couponCode.length() == 8;
	}
	private void decrementCouponQuantity(Coupon coupon) {
        
        PreparedStatement stmt = null;
        try {
            String sql = "UPDATE coupons SET dcpn_no = dcpn_no - 1 WHERE dcpn_code = ?";
            stmt = cn.prepareStatement(sql);
            stmt.setString(1, coupon.getCode());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close the resources
            try {
                if (stmt != null) stmt.close();
                if (cn != null) cn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
