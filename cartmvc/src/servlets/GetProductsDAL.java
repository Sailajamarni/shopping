package servlets;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetProductsDAL {

	private String url;
	private String username;
	private String password;
	private String driver;
	private List<Products> productsList;
	private Connection cn;

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
			// Handle connection initialization exception
			throw new RuntimeException("Failed to initialize database connection", e);
		}
	}

	public List<Products> getProducts(int categoryId) {
		String query = "SELECT * FROM public.\"Products_pr\" WHERE pcategoryid = ?";
		try (PreparedStatement pst = cn.prepareStatement(query)) {
			pst.setInt(1, categoryId);
			try (ResultSet rs = pst.executeQuery()) {
				while (rs.next()) {
					int pid = rs.getInt("pid");
					String pname = rs.getString("pname");
					double pprice = rs.getDouble("pprice");
					String pimag = rs.getString("pimg");
					int hsncode = rs.getInt("phsncode");
					Products product = new Products(pid, pname, pprice, pimag, categoryId, hsncode);
					productsList.add(product);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return productsList;
	}

	public Products getProductById(int id) {
		Products p = null;
		String query = "SELECT * FROM public.\"Products_pr\" WHERE pid = ?";

		try (PreparedStatement pst = cn.prepareStatement(query)) {
			System.out.println("connection");
			pst.setInt(1, id);
			try (ResultSet rs = pst.executeQuery()) {
				if (rs.next()) {
					int pid = rs.getInt("pid");
					int pcatid = rs.getInt("pcategoryid");
					String pname = rs.getString("pname");
					double pprice = rs.getDouble("pprice");
					String pimag = rs.getString("pimg");
					int hsncode = rs.getInt("phsncode");
					p = new Products(pid, pname, pprice, pimag, pcatid, hsncode);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return p;
	}

	public double getGSTForProduct(int hsnCode) {
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
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (cn != null)
					cn.close();
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

			String sql = "SELECT price FROM public.\"Products_pr\" WHERE pid = ?";
			stmt = cn.prepareStatement(sql);
			stmt.setInt(1, productId);
			rs = stmt.executeQuery();
			if (rs.next()) {
				productPrice = rs.getDouble("pprice");
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

	public Products getProductByhsnCode(int hsncode) {
		String query = "SELECT * FROM public.\"Products_pr\" WHERE phsncode = ?";
		Products p = null;
		try (PreparedStatement pst = cn.prepareStatement(query)) {
			pst.setInt(1, hsncode);
			try (ResultSet rs = pst.executeQuery()) {
				if (rs.next()) {
					int pid = rs.getInt("pid");
					int pcatid = rs.getInt("pcategoryid");
					String pname = rs.getString("pname");
					double pprice = rs.getDouble("pprice");
					String pimag = rs.getString("pimg");
					int hsncode1 = rs.getInt("phsncode");
					p = new Products(pid, pname, pprice, pimag, pcatid, hsncode1);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return p;
	}

	public Map<Integer, Double> getAllProducts() throws SQLException {
		Map<Integer, Double> products = new HashMap<>();

		// SQL query to retrieve product IDs and prices
		String query = "SELECT pid, pprice FROM public.\"Products_pr\"";

		try (PreparedStatement statement = cn.prepareStatement(query); ResultSet resultSet = statement.executeQuery()) {

			// Iterate over the result set and populate the products map
			while (resultSet.next()) {
				int productId = resultSet.getInt("pid");
				double price = resultSet.getDouble("pprice");
				products.put(productId, price);
			}
		}

		return products;
	}
}
