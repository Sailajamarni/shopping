
package servlets;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/AddToCartController")
public class AddToCartController extends HttpServlet {
	private GetProductsDAL productsDAL; // Declare GetProductsDAL instance variable

	@Override
	public void init() throws ServletException {
		super.init();
		// Initialize GetProductsDAL instance in servlet's init method
		productsDAL = new GetProductsDAL();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		int prodId = Integer.parseInt(request.getParameter("pid"));

		// Call instance method on GetProductsDAL instance
		Products product = productsDAL.getProductById(prodId);
		if (product != null) {
			double productPrice = product.getPprice();
			int hsnCode = product.getHsncode();

			// Retrieve GST based on HSN ID from the database
			double gst = productsDAL.getGSTForProduct(hsnCode);

			// Calculate price inclusive of GST
			double priceInclusiveOfGST = productPrice * (1 + (gst / 100));

			HttpSession session = request.getSession();
			List<CartItem> cartItems = (List<CartItem>) session.getAttribute("cartitems");
			Map<Integer, Double> productsMap = null;
			try {
				productsMap = productsDAL.getAllProducts();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			// Assuming this method retrieves all products
			request.setAttribute("products", productsMap);
			if (cartItems == null) {
				cartItems = new ArrayList<>();
			}
			boolean productExists = false;
			for (CartItem item : cartItems) {
				if (item.getPid() == prodId) {
					item.setQuantity(item.getQuantity() + 1);
					productExists = true;
					break;
				}
			}

			if (!productExists) {
				// Create a new CartItem with the calculated price inclusive of GST
				CartItem item = new CartItem(prodId, 1, priceInclusiveOfGST, hsnCode);
				cartItems.add(item);
			}
			session.setAttribute("cartitems", cartItems);
			RequestDispatcher dispatcher = request.getRequestDispatcher("cart.jsp");
			dispatcher.forward(request, response);
		} else {
			// Handle case where product details are not found
			// For example, redirect to an error page or display an error message
			response.sendRedirect("error.jsp");
		}
	}

	private static final long serialVersionUID = 1L;
}
