package servlets;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ShippingChargesCalculator {

	// Assume you have a database connection
	private String url;
	private String username;
	private String password;
	private String driver;
	private Connection cn;

	// Constructor to initialize the connection
	public ShippingChargesCalculator() {
		url = DatabaseConnection.getUrl();
		username = DatabaseConnection.getUsername();
		password = DatabaseConnection.getPassword();
		driver = DatabaseConnection.getDriver();

		try {
			Class.forName(driver);
			cn = DriverManager.getConnection(url, username, password);
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
			// Handle connection initialization exception
			throw new RuntimeException("Failed to initialize database connection", e);
		}
	}

	public Map<Integer, Double> calculateShippingCharges(double orderValue, Map<Integer, Double> products)
			throws SQLException {
		Map<Integer, Double> shippingChargesMap = new HashMap<>();

		// Fetch shipping charges from the database based on order value, pin code
		double shippingCharges = getShippingCharges(orderValue);

		// Calculate total product value
		double totalProductValue = 0.0;
		for (double productValue : products.values()) {
			totalProductValue += productValue;
		}

		// Allocate shipping charges to individual products on pro-rata basis
		for (Map.Entry<Integer, Double> entry : products.entrySet()) {
			int productId = entry.getKey();
			double productValue = entry.getValue();
			double productShare = (productValue / totalProductValue) * shippingCharges;
			shippingChargesMap.put(productId, productShare);
		}

		// Apply GST on shipping charges for each product
		applyGSTOnShippingCharges(shippingChargesMap);

		return shippingChargesMap;
	}

	private double getShippingCharges(double orderValue) {
		double shippingCharges = 0.0;

		// Implement logic to fetch shipping charges from the database based on order value and pin code
		String query = "SELECT orvl_shippingamount FROM OrderValueWiseShippingCharges "
				+ "WHERE ? BETWEEN orvl_from AND orvl_to";
		try (PreparedStatement statement = cn.prepareStatement(query)) {
			statement.setDouble(1, orderValue);
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				shippingCharges = resultSet.getDouble("orvl_shippingamount");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return shippingCharges;
	}

	private void applyGSTOnShippingCharges(Map<Integer, Double> shippingChargesMap) throws SQLException {
		for (Map.Entry<Integer, Double> entry : shippingChargesMap.entrySet()) {
			int productId = entry.getKey();
			double shippingCharges = entry.getValue();
			double gstPercentage = getGstPercentageForProduct(productId);
			double gstAmount = (gstPercentage / 100) * shippingCharges;
			shippingCharges += gstAmount;
			shippingChargesMap.put(productId, shippingCharges);
		}
	}

	private double getGstPercentageForProduct(int productId) throws SQLException {
		double gstPercentage = 0.0;
		// Implement logic to fetch GST percentage from the database based on product category or use predefined values
		String str = "SELECT hsnc_gstc_percentage FROM public.\"Products_pr\" p "
				+ "JOIN HSNCodes_1 h ON p.hsnId = h.hsnc_id " + "WHERE p.id = ?";
		try (PreparedStatement stmt = cn.prepareStatement(str)) {
			stmt.setInt(1, productId);
			ResultSet resultSet = stmt.executeQuery();
			if (resultSet.next()) {
				gstPercentage = resultSet.getDouble("hsnc_gstc_percentage");
			}
		}
		return gstPercentage;
	}
}
