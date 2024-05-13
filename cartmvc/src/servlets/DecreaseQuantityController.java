package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.*;


public class DecreaseQuantityController extends HttpServlet
{

    public DecreaseQuantityController()
    {
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        int prodId = Integer.parseInt(request.getParameter("pid"));
        HttpSession session = request.getSession();
        List cartItems = (List)session.getAttribute("cartitems");
        if(cartItems != null)
        {
            Iterator iterator = cartItems.iterator();
            while(iterator.hasNext()) 
            {
                CartItem item = (CartItem)iterator.next();
                if(item.getPid() != prodId)
                    continue;
                int newQuantity = item.getQuantity() - 1;
                if(newQuantity < 0)
                    continue;
                item.setQuantity(newQuantity);
                break;
            }
        }
        session.setAttribute("cartitems", cartItems);
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("success");
    }

    private static final long serialVersionUID = 1L;
}
