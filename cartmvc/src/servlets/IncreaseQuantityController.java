package servlets;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class IncreaseQuantityController extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int prodId = Integer.parseInt(request.getParameter("pid"));
        HttpSession session = request.getSession();
        List<CartItem> cartItems = (List<CartItem>) session.getAttribute("cartitems");
        if (cartItems != null) {
            for (CartItem item : cartItems) {
                if (item.getPid() == prodId) {
                    item.setQuantity(item.getQuantity() + 1);
                    break;
                }
            }
        }
        session.setAttribute("cartitems", cartItems);
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("success");
    }

    private static final long serialVersionUID = 1L;
}
