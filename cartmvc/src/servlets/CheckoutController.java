package servlets;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.*;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class CheckoutController extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String cartItemsJson = request.getParameter("cartitems");
        Gson gson = new Gson();
        CartItem[] cartItems = gson.fromJson(cartItemsJson, CartItem[].class);
        List<CartItem> cartItemList = Arrays.asList(cartItems);
        if (cartItemList.isEmpty()) {
            JsonObject jsonResponse = new JsonObject();
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("error", "Cart is empty. Add items to cart before checkout.");
            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            out.print(jsonResponse.toString());
            out.flush();
            return;
        }
        int customerId = 123;
        int orderId = OrdersDAL.ord_id;
        System.out.println(orderId);
        double orderTotal = calculateTotalOrderCost(cartItemList);
        System.out.println(orderTotal);
        OrdersDAL orders = new OrdersDAL();
        boolean insertedIntoOrders = orders.insertOrder(orderTotal, customerId);
        if (insertedIntoOrders)
            System.out.println("inserted into orders");
        boolean insertedIntoProductOrders = false;
        ProductOrdersDAL productOrders = new ProductOrdersDAL();
        for (CartItem item : cartItemList) {
            Products product = new GetProductsDAL().getProductById(item.getPid());
            insertedIntoProductOrders = productOrders.insertProductOrder(orderId, item.getPid(), item.getQuantity(), product.getPprice());
            if (!insertedIntoProductOrders) {
                System.out.println("could not insert into productorders");
                break;
            }
        }

        JsonObject jsonResponse = new JsonObject();
        if (insertedIntoOrders && insertedIntoProductOrders) {
            HttpSession session = request.getSession();
            session.removeAttribute("cartitems");
            jsonResponse.addProperty("success", true);
            jsonResponse.addProperty("totalAmount", orderTotal);
        } else {
            jsonResponse.addProperty("success", false);
        }
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.print(jsonResponse.toString());
        out.flush();
    }

    private double calculateTotalOrderCost(List<CartItem> cartItems) {
        double totalCost = 0.0;
        for (CartItem item : cartItems) {
            Products product = new GetProductsDAL().getProductById(item.getPid());
            if (product != null)
                totalCost += item.getQuantity() * product.getPprice();
        }
        return totalCost;
    }

    private static final long serialVersionUID = 1L;
}
