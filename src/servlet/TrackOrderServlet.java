import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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

        response.setContentType("text/html;charset=UTF-8");

        PrintWriter out = response.getWriter();

        // =========================================
        // CHECK LOGIN
        // =========================================

        HttpSession session = request.getSession(false);

        if (session == null ||
                session.getAttribute("userId") == null) {

            out.println("""
                    <h2>Please login first.</h2>
                    <a href="login.html">Login</a>
                    """);

            return;
        }

        Integer userId =
                (Integer) session.getAttribute("userId");


        // =========================================
        // GET ORDER ID
        // =========================================

        String orderIdText =
                request.getParameter("orderId");

        if (orderIdText == null ||
                orderIdText.trim().isEmpty()) {

            out.println("""
                    <h2>Order ID is missing.</h2>
                    <a href="index.html">Back to Home</a>
                    """);

            return;
        }


        int orderId;

        try {

            orderId =
                    Integer.parseInt(orderIdText);

        } catch (NumberFormatException e) {

            out.println("""
                    <h2>Invalid Order ID.</h2>
                    <a href="index.html">Back to Home</a>
                    """);

            return;
        }


        // =========================================
        // DATABASE DETAILS
        // =========================================

        String url = System.getenv("DB_URL");
        String username = System.getenv("DB_USERNAME");
        String password = System.getenv("DB_PASSWORD");

        if (url == null ||
                username == null ||
                password == null) {

            out.println("""
                    <h2>Database configuration is missing.</h2>
                    """);

            return;
        }

        if (url.startsWith("mysql://")) {
            url = "jdbc:" + url;
        }


        // =========================================
        // SQL
        // =========================================

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

                        out.println("""
                                <h2>Order not found.</h2>
                                <p>
                                    This order does not belong
                                    to your account.
                                </p>
                                <a href="index.html">
                                    Back to Home
                                </a>
                                """);

                        return;
                    }


                    // =========================================
                    // GET ORDER DATA
                    // =========================================

                    String customerName =
                            rs.getString("customer_name");

                    String foodName =
                            rs.getString("food_name");

                    int quantity =
                            rs.getInt("quantity");

                    String status =
                            rs.getString("status");

Double deliveryLatitude =
        (Double) rs.getObject("delivery_latitude");

Double deliveryLongitude =
        (Double) rs.getObject("delivery_longitude");
                    // =========================================
                    // TRACKING PAGE
                    // =========================================

                    out.println("""
                            <!DOCTYPE html>
                            <html>

                            <head>

                                <title>Track Order</title>

                                <style>

                                    body {
                                        font-family: Arial, sans-serif;
                                        background: #fff8f1;
                                        text-align: center;
                                        padding: 40px;
                                    }

                                    .container {
                                        max-width: 650px;
                                        margin: auto;
                                        background: white;
                                        padding: 30px;
                                        border-radius: 15px;
                                        box-shadow:
                                            0 4px 15px
                                            rgba(0,0,0,0.1);
                                    }

                                    h1 {
                                        color: #ff6b00;
                                    }

                                    .order-info {
                                        text-align: left;
                                        margin: 25px 0;
                                        padding: 20px;
                                        background: #fff3e8;
                                        border-radius: 10px;
                                    }

                                    .steps {
                                        display: flex;
                                        justify-content:
                                            space-between;
                                        margin-top: 40px;
                                        position: relative;
                                    }

                                    .step {
                                        width: 23%;
                                    }

                                    .circle {
                                        width: 45px;
                                        height: 45px;
                                        line-height: 45px;
                                        margin: auto;
                                        border-radius: 50%;
                                        background: #ddd;
                                        font-weight: bold;
                                    }

                                    .active {
                                        background: #ff6b00;
                                        color: white;
                                    }

                                    .step p {
                                        font-size: 13px;
                                        margin-top: 10px;
                                    }

                                    .home-btn {
                                        display: inline-block;
                                        margin-top: 30px;
                                        padding: 12px 20px;
                                        background: #ff6b00;
                                        color: white;
                                        text-decoration: none;
                                        border-radius: 8px;
                                    }

                                </style>

                            </head>

                            <body>

                                <div class="container">

                                    <h1>📦 Track Your Order</h1>

                                    <div class="order-info">

                                        <p>
                                            <strong>Order ID:</strong>
                                            """ + orderId + """
                                        </p>

                                        <p>
                                            <strong>Customer:</strong>
                                            """ + escapeHtml(customerName) + """
                                        </p>

                                        <p>
                                            <strong>Food:</strong>
                                            """ + escapeHtml(foodName) + """
                                        </p>

                                        <p>
                                            <strong>Quantity:</strong>
                                            """ + quantity + """
                                        </p>

                                        <p>
                                            <strong>Current Status:</strong>
                                            """ + escapeHtml(status) + """
                                        </p>

                                    </div>

                                    <div class="steps">

                                        <div class="step">
                                            <div class="circle
                            """ + getActiveClass(status, "Order Placed") + """
                                            ">
                                                ✓
                                            </div>
                                            <p>Order Placed</p>
                                        </div>


                                        <div class="step">
                                            <div class="circle
                            """ + getActiveClass(status, "Preparing") + """
                                            ">
                                                ✓
                                            </div>
                                            <p>Preparing</p>
                                        </div>


                                        <div class="step">
                                            <div class="circle
                            """ + getActiveClass(status, "Out for Delivery") + """
                                            ">
                                                ✓
                                            </div>
                                            <p>Out for Delivery</p>
                                        </div>


                                        <div class="step">
                                            <div class="circle
                            """ + getActiveClass(status, "Delivered") + """
                                            ">
                                                ✓
                                            </div>
                                            <p>Delivered</p>
                                        </div>

                                    </div>

                                    <a href="index.html"
                                       class="home-btn">
                                        ← Back to Home
                                    </a>

                                </div>

                            </body>

                            </html>
                            """);
                }
            }

        } catch (Exception e) {

            e.printStackTrace();

            out.println("""
                    <h2>Something went wrong.</h2>
                    <p>
                    """ + escapeHtml(e.getMessage()) + """
                    </p>
                    """);
        }
    }


    // =========================================
    // DETERMINE ACTIVE STEP
    // =========================================

    private String getActiveClass(
            String currentStatus,
            String stepStatus) {

        String[] statuses = {
            "Order Placed",
            "Preparing",
            "Out for Delivery",
            "Delivered"
        };

        int currentIndex = getStatusIndex(currentStatus);
        int stepIndex = getStatusIndex(stepStatus);

        if (stepIndex <= currentIndex) {
            return " active";
        }

        return "";
    }


    private int getStatusIndex(String status) {

        if ("Preparing".equals(status)) {
            return 1;
        }

        if ("Out for Delivery".equals(status)) {
            return 2;
        }

        if ("Delivered".equals(status)) {
            return 3;
        }

        return 0;
    }


    // =========================================
    // HTML ESCAPE
    // =========================================

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