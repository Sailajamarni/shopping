package servlets;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/GetProductsController")
public class GetProductsController extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // Retrieve category_id parameter from the request
            String categoryIdParam = request.getParameter("category_id");

            // Check if category_id parameter is provided
            if (categoryIdParam != null && !categoryIdParam.isEmpty()) {
                // Parse category_id to integer
                int categoryId = Integer.parseInt(categoryIdParam);

                // Fetch products from database based on category_id
                GetProductsDAL productsDAL = new GetProductsDAL();
                List<Products> productsList = productsDAL.getProducts(categoryId);

                // Set response content type to application/json
                response.setContentType("application/json");
                PrintWriter pw = response.getWriter();

                // Convert productsList to JSON using Gson library
                Gson gson = new Gson();
                String json = gson.toJson(productsList);

                // Write JSON response to PrintWriter
                pw.println(json);
            } else {
                // If category_id parameter is missing or empty, send 400 Bad Request response
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().println("Please provide a valid category_id parameter.");
            }
        } catch (NumberFormatException e) {
            // Handle NumberFormatException if category_id parameter cannot be parsed to integer
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Invalid category_id parameter. It should be an integer.");
        }
    }
}
