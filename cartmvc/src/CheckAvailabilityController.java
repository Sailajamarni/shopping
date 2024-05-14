package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/CheckAvailabilityController")
public class CheckAvailabilityController extends HttpServlet {

	private String url;
	private String username;
	private String password;
	private String driver;

	public CheckAvailabilityController() {
		url = DatabaseConnection.getUrl();
		username = DatabaseConnection.getUsername();
		password = DatabaseConnection.getPassword();
		driver = DatabaseConnection.getDriver();
		try {
			Class.forName(driver);
			Connection cn = DriverManager.getConnection(url, username, password);
			System.out.println("connected");
		} catch (SQLException e1) {
			e1.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		int pincode = Integer.parseInt(request.getParameter("pincode"));
		System.out.println(pincode);
		int categoryId = Integer.parseInt(request.getParameter("category_id"));
		boolean isServiceable = isPincodeServiceable(pincode, categoryId);
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		out.print("{\"isServiceable\": " + isServiceable + "}");
		out.flush();
	}

	boolean isPincodeServiceable(int pincode, int category) {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = DriverManager.getConnection(url, username, password);
			stmt = conn.prepareStatement(
					"SELECT COUNT(*) FROM ServiceableRegions_1 sr JOIN ProductCategoryWiseServiceableRegions_1 pr ON sr.srrg_id = pr.srrg_id WHERE ? BETWEEN sr.srrg_pinfrom AND sr.srrg_pinto AND pr.prct_id = ?");
			stmt.setInt(1, pincode);
			stmt.setInt(2, category);
			rs = stmt.executeQuery();
			System.out.println("getting..1");
			if (rs.next()) {
				return rs.getInt(1) > 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
}