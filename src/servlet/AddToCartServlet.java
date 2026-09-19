package servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/AddToCartServlet")
public class AddToCartServlet extends HttpServlet {

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String foodIdParam = request.getParameter("foodId");

        if (foodIdParam == null || foodIdParam.isEmpty()) {
            response.sendRedirect("MenuServlet");
            return;
        }

        int foodId;

        try {
            foodId = Integer.parseInt(foodIdParam);
        } catch (NumberFormatException e) {
            response.sendRedirect("MenuServlet");
            return;
        }

        String url = System.getenv("DB_URL");
        String username = System.getenv("DB_USERNAME");
        String password = System.getenv("DB_PASSWORD");

        if (url == null || username == null || password == null) {
            response.getWriter().println(
                    "<h2>Database environment variables are missing.</h2>"
            );
            return;
        }

        if (url.startsWith("mysql://")) {
            url = "jdbc:" + url;
        }

        String sql = """
                SELECT id, name, price
                FROM foods
                WHERE id = ? AND available = TRUE
                """;

        try (
                Connection con =
                        DriverManager.getConnection(
                                url,
                                username,
                                password
                        );

                PreparedStatement ps =
                        con.prepareStatement(sql)
        ) {

            ps.setInt(1, foodId);

            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) {
                    response.sendRedirect("MenuServlet");
                    return;
                }

                int id = rs.getInt("id");
                String name = rs.getString("name");
                String price = rs.getBigDecimal("price").toString();

                HttpSession session = request.getSession();

                List<Map<String, String>> cart =
                        (List<Map<String, String>>)
                        session.getAttribute("cart");

                if (cart == null) {
                    cart = new ArrayList<>();
                }

                boolean found = false;

                for (Map<String, String> item : cart) {

                    if (item.get("id").equals(String.valueOf(id))) {

                        int quantity =
                                Integer.parseInt(
                                        item.get("quantity")
                                );

                        item.put(
                                "quantity",
                                String.valueOf(quantity + 1)
                        );

                        found = true;

                        break;
                    }
                }

                if (!found) {

                    Map<String, String> item =
                            new HashMap<>();

                    item.put(
                            "id",
                            String.valueOf(id)
                    );

                    item.put(
                            "name",
                            name
                    );

                    item.put(
                            "price",
                            price
                    );

                    item.put(
                            "quantity",
                            "1"
                    );

                    cart.add(item);
                }

                session.setAttribute("cart", cart);

                response.sendRedirect("cart.jsp");
            }

        } catch (Exception e) {

            response.setContentType(
                    "text/html;charset=UTF-8"
            );

            response.getWriter().println(
                    "<h2>Unable to add item to cart.</h2>"
            );

            response.getWriter().println(
                    "<p>" + e.getMessage() + "</p>"
            );
        }
    }
}