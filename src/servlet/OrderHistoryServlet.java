import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/OrderHistoryServlet")
public class OrderHistoryServlet extends HttpServlet {

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null ||
                session.getAttribute("userId") == null) {

            response.sendRedirect("login.html");
            return;
        }

        int userId;

        try {
            userId = Integer.parseInt(
                    session.getAttribute("userId").toString()
            );
        } catch (Exception e) {
            response.sendRedirect("login.html");
            return;
        }

        String dbUrl = System.getenv("DB_URL");
        String dbUsername = System.getenv("DB_USERNAME");
        String dbPassword = System.getenv("DB_PASSWORD");

        if (dbUrl == null ||
                dbUsername == null ||
                dbPassword == null) {

            response.sendError(
                    500,
                    "Database configuration is missing"
            );
            return;
        }

        if (dbUrl.startsWith("mysql://")) {
            dbUrl = "jdbc:" + dbUrl;
        }

        String sql = """
                SELECT
                    id,
                    food_name,
                    quantity,
                    status
                FROM orders
                WHERE user_id = ?
                ORDER BY id DESC
                """;

        List<Map<String, Object>> orders =
                new ArrayList<>();

        try {

            Class.forName("com.mysql.cj.jdbc.Driver");

            try (
                Connection con =
                        DriverManager.getConnection(
                                dbUrl,
                                dbUsername,
                                dbPassword
                        );

                PreparedStatement ps =
                        con.prepareStatement(sql)
            ) {

                ps.setInt(1, userId);

                try (ResultSet rs = ps.executeQuery()) {

                    while (rs.next()) {

                        Map<String, Object> order =
                                new HashMap<>();

                        order.put(
                                "id",
                                rs.getInt("id")
                        );

                        order.put(
                                "foodName",
                                rs.getString("food_name")
                        );

                        order.put(
                                "quantity",
                                rs.getInt("quantity")
                        );

                        order.put(
                                "status",
                                rs.getString("status")
                        );

                        orders.add(order);
                    }
                }
            }

            request.setAttribute(
                    "orders",
                    orders
            );

            RequestDispatcher dispatcher =
                    request.getRequestDispatcher(
                            "/order-history.jsp"
                    );

            dispatcher.forward(
                    request,
                    response
            );

        } catch (Exception e) {

            e.printStackTrace();

            response.sendError(
                    500,
                    "Unable to load order history"
            );
        }
    }
}