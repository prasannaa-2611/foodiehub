package servlet;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/RemoveFromCartServlet")
public class RemoveFromCartServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String foodId = request.getParameter("foodId");

        HttpSession session = request.getSession();
        List<Map<String, String>> cart =
                (List<Map<String, String>>) session.getAttribute("cart");

        if (cart != null && foodId != null) {

            cart.removeIf(item -> foodId.equals(item.get("id")));

            session.setAttribute("cart", cart);
        }

        response.sendRedirect("cart.jsp");
    }
}