package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

@WebServlet("/CheckoutController")
public class CheckoutController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String cartItemsJson = request.getParameter("cartitems");
		Gson gson = new Gson();
		CartItem[] cartItemss = gson.fromJson(cartItemsJson, CartItem[].class);

		List<CartItem> cartItems = Arrays.asList(cartItemss);
		if (cartItems.isEmpty()) {
			JsonObject jsonResponse = new JsonObject();
			jsonResponse.addProperty("success", false);
			jsonResponse.addProperty("error", "Cart is empty. Add items to cart before checkout.");
			response.setContentType("application/json");
			PrintWriter out = response.getWriter();
			out.print(jsonResponse.toString());
			out.flush();
			return; // Exit the method
		}
		int customerId = 123;

		int orderId = OrdersDAL.ord_id;

		System.out.println(orderId);

		double orderTotal = calculateTotalOrderCost(cartItems);
		System.out.println(orderTotal);

		OrdersDAL orders = new OrdersDAL();
		boolean b1 = orders.insertOrder(orderTotal, customerId);
		if (b1)
			System.out.println("inserted into orders");

		boolean b2 = false;
		ProductOrdersDAL productorders = new ProductOrdersDAL();
		for (CartItem item : cartItems) {
			GetProductsDAL getProductsDAL = new GetProductsDAL();
			Products product = getProductsDAL.getProductById(item.getPid());
			Products prod = new GetProductsDAL().getProductByhsnCode(product.getHsncode());
			b2 = productorders.insertProductOrder(orderId, item.getPid(), item.getQuantity(), product.getPprice());
			if (!b2) {
				System.out.println("could not insert into productorders");
				break;
			}
		}
		JsonObject jsonResponse = new JsonObject();
		if (b1 && b2) {
			HttpSession session = request.getSession();
			session.removeAttribute("cartitems");
			jsonResponse.addProperty("success", true);
			jsonResponse.addProperty("totalAmount", orderTotal);
		} else {
			jsonResponse.addProperty("success", false);
		}

		// int pinCode = Integer.parseInt(request.getParameter("pinCode"));
		Map<Integer, Double> products = (Map<Integer, Double>) request.getAttribute("products");
		Connection connection = (Connection) getServletContext().getAttribute("dbConnection");
		ShippingChargesCalculator calculator = new ShippingChargesCalculator();

		try {
			// Calculate shipping charges including GST
			Map<Integer, Double> shippingCharges = calculator.calculateShippingCharges(orderTotal, products);

			// Set the shipping charges attribute in request scope
			request.setAttribute("shippingCharges", shippingCharges);

			// Continue with the rest of your checkout process...

			// Redirect to checkout.jsp
			request.getRequestDispatcher("checkout.jsp").forward(request, response);

		} catch (SQLException e) {
			e.printStackTrace();
			// Handle database exception
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		out.print(jsonResponse.toString());
		out.flush();

	}

	private double calculateTotalOrderCost(List<CartItem> cartItems) {
		double totalCost = 0;
		for (CartItem item : cartItems) {
			Products product = new GetProductsDAL().getProductById(item.getPid());
			if (product != null) {
				totalCost += item.getQuantity() * product.getPprice();
			}
		}
		return totalCost;
	}

}
