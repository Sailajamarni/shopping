package servlets;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/ApplyCouponController")
public class ApplyCouponController extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	String couponCode = request.getParameter("couponCode");

        // Create an instance of CouponService
        CouponService couponService = new CouponService();

        // Apply the coupon and decrement its quantity
        boolean couponApplied=false;
		try {
			couponApplied = couponService.applyCouponAndDecrementQuantity(couponCode);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        if (couponApplied) {
            // Coupon applied successfully, perform further actions
            response.sendRedirect("cart.jsp?couponApplied=true");
        } else {
            // Coupon not applied (invalid or out of stock), handle accordingly
            response.sendRedirect("cart.jsp?couponApplied=false");
        }
    }
}
