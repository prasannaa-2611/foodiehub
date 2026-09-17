import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/TrackOrderServlet")
public class TrackOrderServlet extends HttpServlet {

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        // Check login
        HttpSession session = request.getSession(false);

        if (session == null ||
                session.getAttribute("userId") == null) {

            response.sendRedirect("login.html");
            return;
        }

        // Get logged-in user's ID
        int userId;

        try {
            userId = Integer.parseInt(
                    session.getAttribute("userId").toString()
            );
        } catch (Exception e) {
            response.sendRedirect("login.html");
            return;
        }

        // Get order ID
        String orderIdText =
                request.getParameter("orderId");

        if (orderIdText == null ||
                orderIdText.trim().isEmpty()) {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Order ID is missing"
            );
            return;
        }

        int orderId;

        try {
            orderId = Integer.parseInt(orderIdText);
        } catch (NumberFormatException e) {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid Order ID"
            );
            return;
        }

        // Database configuration
        String url = System.getenv("DB_URL");
        String username = System.getenv("DB_USERNAME");
        String password = System.getenv("DB_PASSWORD");

        if (url == null ||
                username == null ||
                password == null) {

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Database configuration is missing"
            );
            return;
        }

        if (url.startsWith("mysql://")) {
            url = "jdbc:" + url;
        }

        String sql = """
                SELECT
                    id,
                    customer_name,
                    food_name,
                    quantity,
                    status,
                    delivery_latitude,
                    delivery_longitude
                FROM orders
                WHERE id = ?
                AND user_id = ?
                """;

        try {

            Class.forName("com.mysql.cj.jdbc.Driver");

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

                ps.setInt(1, orderId);
                ps.setInt(2, userId);

                try (ResultSet rs = ps.executeQuery()) {

                    if (!rs.next()) {

                        response.sendError(
                                HttpServletResponse.SC_NOT_FOUND,
                                "Order not found"
                        );
                        return;
                    }

                    String customerName =
                            rs.getString("customer_name");

                    String foodName =
                            rs.getString("food_name");

                    int quantity =
                            rs.getInt("quantity");

                    String status =
                            rs.getString("status");

                    Double latitude =
                            (Double) rs.getObject(
                                    "delivery_latitude"
                            );

                    Double longitude =
                            (Double) rs.getObject(
                                    "delivery_longitude"
                            );

                    // Send order information to JSP
                    request.setAttribute(
                            "orderId",
                            orderId
                    );

                    request.setAttribute(
                            "customerName",
                            escapeHtml(customerName)
                    );

                    request.setAttribute(
                            "foodName",
                            escapeHtml(foodName)
                    );

                    request.setAttribute(
                            "quantity",
                            quantity
                    );

                    request.setAttribute(
                            "status",
                            escapeHtml(status)
                    );

                    // Send GPS information to JSP
                    if (latitude != null &&
                            longitude != null) {

                        request.setAttribute(
                                "deliveryLatitude",
                                latitude
                        );

                        request.setAttribute(
                                "deliveryLongitude",
                                longitude
                        );

                        request.setAttribute(
                                "locationMessage",
                                "Delivery location available 📍"
                        );

                    } else {

                        request.setAttribute(
                                "deliveryLatitude",
                                null
                        );

                        request.setAttribute(
                                "deliveryLongitude",
                                null
                        );

                        request.setAttribute(
                                "locationMessage",
                                "Waiting for delivery location..."
                        );
                    }

                    // Status CSS class
                    String statusClass = "status-default";

                    if ("Order Placed".equalsIgnoreCase(status)) {

                        statusClass = "status-placed";

                    } else if ("Preparing".equalsIgnoreCase(status)) {

                        statusClass = "status-preparing";

                    } else if ("Out for Delivery".equalsIgnoreCase(status)) {

                        statusClass = "status-delivery";

                    } else if ("Delivered".equalsIgnoreCase(status)) {

                        statusClass = "status-delivered";
                    }

                    request.setAttribute(
                            "statusClass",
                            statusClass
                    );

                    // Open tracking JSP
                    RequestDispatcher dispatcher =
                            request.getRequestDispatcher(
                                    "/track-order.jsp"
                            );

                    dispatcher.forward(
                            request,
                            response
                    );
                }
            }

        } catch (Exception e) {

            e.printStackTrace();

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to load order"
            );
        }
    }

    private String escapeHtml(String text) {

        if (text == null) {
            return "";
        }

        return text
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}