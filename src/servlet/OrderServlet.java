
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
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

@WebServlet("/OrderServlet")
public class OrderServlet extends HttpServlet {

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        PrintWriter out = response.getWriter();

        // =========================================
        // GET ORDER DETAILS
        // =========================================

        String customerName = request.getParameter("customerName");
        String foodName = request.getParameter("foodName");
        String quantityText = request.getParameter("quantity");


        // =========================================
        // GET LOGGED-IN USER FROM SESSION
        // =========================================

        HttpSession session = request.getSession(false);

        if (session == null ||
                session.getAttribute("email") == null) {

            showError(out,
                    "User email not found. Please login again.");
            return;
        }

        String customerEmail =
                (String) session.getAttribute("email");

        Integer userId =
                (Integer) session.getAttribute("userId");

        if (userId == null) {

            showError(out,
                    "User ID not found. Please login again.");
            return;
        }


        // =========================================
        // GET ADDRESS DETAILS
        // =========================================

        String fullName = request.getParameter("fullName");
        String phone = request.getParameter("phone");
        String addressLine = request.getParameter("addressLine");
        String city = request.getParameter("city");
        String state = request.getParameter("state");
        String pincode = request.getParameter("pincode");


        // =========================================
        // VALIDATION
        // =========================================

        if (isEmpty(customerName) ||
                isEmpty(foodName) ||
                isEmpty(quantityText) ||
                isEmpty(fullName) ||
                isEmpty(phone) ||
                isEmpty(addressLine) ||
                isEmpty(city) ||
                isEmpty(state) ||
                isEmpty(pincode)) {

            showError(out,
                    "Please fill all required fields.");
            return;
        }


        // =========================================
        // QUANTITY
        // =========================================

        int quantity;

        try {

            quantity = Integer.parseInt(quantityText);

            if (quantity < 1 || quantity > 20) {

                showError(out,
                        "Quantity must be between 1 and 20.");
                return;
            }

        } catch (NumberFormatException e) {

            showError(out,
                    "Invalid quantity.");
            return;
        }


        // =========================================
        // DATABASE ENVIRONMENT VARIABLES
        // =========================================

        String url = System.getenv("DB_URL");
        String username = System.getenv("DB_USERNAME");
        String password = System.getenv("DB_PASSWORD");

        if (isEmpty(url) ||
                isEmpty(username) ||
                isEmpty(password)) {

            showError(out,
                    "Database environment variables are missing.");
            return;
        }


        // =========================================
        // MYSQL URL
        // =========================================

        if (url.startsWith("mysql://")) {
            url = "jdbc:" + url;
        }


        // =========================================
        // SQL
        // =========================================

        String orderSql = """
                INSERT INTO orders
                (
                    user_id,
                    customer_name,
                    food_name,
                    quantity,
                    status
                )
                VALUES (?, ?, ?, ?, ?)
                """;


        String addressSql = """
                INSERT INTO addresses
                (
                    customer_name,
                    full_name,
                    phone,
                    address_line,
                    city,
                    state,
                    pincode,
                    is_default
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;


        // =========================================
        // DATABASE CONNECTION
        // =========================================

        try {

            Class.forName("com.mysql.cj.jdbc.Driver");

            try (
                Connection con =
                        DriverManager.getConnection(
                                url,
                                username,
                                password
                        );

                PreparedStatement orderPs =
                        con.prepareStatement(
                                orderSql,
                                java.sql.Statement.RETURN_GENERATED_KEYS
                        );

                PreparedStatement addressPs =
                        con.prepareStatement(addressSql)
            ) {


                // =========================================
                // INSERT ORDER
                // =========================================

                orderPs.setInt(1, userId);

                orderPs.setString(
                        2,
                        customerName.trim()
                );

                orderPs.setString(
                        3,
                        foodName.trim()
                );

                orderPs.setInt(
                        4,
                        quantity
                );

                orderPs.setString(
                        5,
                        "Order Placed"
                );

                orderPs.executeUpdate();


                // =========================================
                // GET GENERATED ORDER ID
                // =========================================

                int orderId = 0;

                try (
                    ResultSet generatedKeys =
                            orderPs.getGeneratedKeys()
                ) {

                    if (generatedKeys.next()) {

                        orderId =
                                generatedKeys.getInt(1);
                    }
                }


                // =========================================
                // INSERT ADDRESS
                // =========================================

                addressPs.setString(
                        1,
                        customerName.trim()
                );

                addressPs.setString(
                        2,
                        fullName.trim()
                );

                addressPs.setString(
                        3,
                        phone.trim()
                );

                addressPs.setString(
                        4,
                        addressLine.trim()
                );

                addressPs.setString(
                        5,
                        city.trim()
                );

                addressPs.setString(
                        6,
                        state.trim()
                );

                addressPs.setString(
                        7,
                        pincode.trim()
                );

                addressPs.setBoolean(
                        8,
                        true
                );

                addressPs.executeUpdate();


                // =========================================
                // SEND ORDER EMAIL
                // =========================================

                try {

                    sendOrderEmail(
                            customerEmail,
                            customerName,
                            foodName,
                            quantity,
                            fullName,
                            phone,
                            addressLine,
                            city,
                            state,
                            pincode
                    );

                } catch (Exception emailException) {

                    emailException.printStackTrace();
                }


                // =========================================
                // SUCCESS PAGE
                // =========================================

                String trackUrl =
                        "TrackOrderServlet?orderId=" + orderId;

                out.println("""
                        <!DOCTYPE html>
                        <html>
                        <head>
                            <title>Order Confirmed</title>

                            <style>

                                body {
                                    font-family: Arial, sans-serif;
                                    background: #fff8f1;
                                    text-align: center;
                                    padding: 40px;
                                }

                                .box {
                                    background: white;
                                    max-width: 600px;
                                    margin: auto;
                                    padding: 30px;
                                    border-radius: 15px;
                                    box-shadow: 0 4px 15px rgba(0,0,0,0.1);
                                }

                                h1 {
                                    color: #ff6b00;
                                }

                                .details {
                                    text-align: left;
                                    margin-top: 20px;
                                    line-height: 1.8;
                                }

                                .home-btn {
                                    display: inline-block;
                                    margin-top: 25px;
                                    padding: 12px 20px;
                                    background: #ff6b00;
                                    color: white;
                                    text-decoration: none;
                                    border-radius: 8px;
                                    margin-right: 10px;
                                }

                                .track-btn {
                                    display: inline-block;
                                    margin-top: 25px;
                                    padding: 12px 20px;
                                    background: #28a745;
                                    color: white;
                                    text-decoration: none;
                                    border-radius: 8px;
                                }

                            </style>
                        </head>

                        <body>

                            <div class="box">

                                <h1>🎉 Order Confirmed!</h1>

                                <p>
                                    Thank you for ordering from FoodieHub.
                                </p>

                                <div class="details">

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
                                        <strong>Status:</strong>
                                        Order Placed
                                    </p>

                                    <h3>Delivery Address</h3>

                                    <p>
                                        """ + escapeHtml(fullName) + """
                                    </p>

                                    <p>
                                        """ + escapeHtml(phone) + """
                                    </p>

                                    <p>
                                        """ + escapeHtml(addressLine) + """
                                    </p>

                                    <p>
                                        """ + escapeHtml(city) + ", "
                                            + escapeHtml(state) + """
                                    </p>

                                    <p>
                                        """ + escapeHtml(pincode) + """
                                    </p>

                                </div>

                                <a href=\"""" + trackUrl + """\"
                                   class="track-btn">
                                    📦 Track Order
                                </a>

                                <a href="index.html"
                                   class="home-btn">
                                    ← Back to Home
                                </a>

                            </div>

                        </body>
                        </html>
                        """);
            }

        } catch (Exception e) {

            e.printStackTrace();

            showError(
                    out,
                    e.getMessage() != null
                            ? e.getMessage()
                            : "Something went wrong."
            );
        }
    }


    // =========================================
    // SEND ORDER EMAIL
    // =========================================

    private void sendOrderEmail(
            String customerEmail,
            String customerName,
            String foodName,
            int quantity,
            String fullName,
            String phone,
            String addressLine,
            String city,
            String state,
            String pincode)
            throws Exception {


        String serviceId =
                System.getenv("EMAILJS_SERVICE_ID");

        String templateId =
                System.getenv("EMAILJS_TEMPLATE_ID");

        String publicKey =
                System.getenv("EMAILJS_PUBLIC_KEY");


        if (isEmpty(serviceId) ||
                isEmpty(templateId) ||
                isEmpty(publicKey)) {

            throw new Exception(
                    "EmailJS environment variables are missing."
            );
        }


        String json = """
                {
                    "service_id": "%s",
                    "template_id": "%s",
                    "user_id": "%s",
                    "template_params": {
                        "customer_name": "%s",
                        "customer_email": "%s",
                        "food_name": "%s",
                        "quantity": "%s",
                        "full_name": "%s",
                        "phone": "%s",
                        "address_line": "%s",
                        "city": "%s",
                        "state": "%s",
                        "pincode": "%s"
                    }
                }
                """.formatted(
                jsonEscape(serviceId),
                jsonEscape(templateId),
                jsonEscape(publicKey),
                jsonEscape(customerName),
                jsonEscape(customerEmail),
                jsonEscape(foodName),
                quantity,
                jsonEscape(fullName),
                jsonEscape(phone),
                jsonEscape(addressLine),
                jsonEscape(city),
                jsonEscape(state),
                jsonEscape(pincode)
        );


        URL url =
                new URL(
                        "https://api.emailjs.com/api/v1.0/email/send"
                );


        HttpURLConnection connection =
                (HttpURLConnection) url.openConnection();


        connection.setRequestMethod("POST");

        connection.setRequestProperty(
                "Content-Type",
                "application/json"
        );

        connection.setDoOutput(true);


        byte[] data =
                json.getBytes(StandardCharsets.UTF_8);


        connection.getOutputStream().write(data);


        int responseCode =
                connection.getResponseCode();


        if (responseCode < 200 ||
                responseCode >= 300) {

            throw new Exception(
                    "EmailJS failed with HTTP "
                            + responseCode
            );
        }


        System.out.println(
                "Order confirmation email sent successfully."
        );
    }


    // =========================================
    // CHECK EMPTY
    // =========================================

    private boolean isEmpty(String value) {

        return value == null ||
                value.trim().isEmpty();
    }


    // =========================================
    // JSON ESCAPE
    // =========================================

    private String jsonEscape(String text) {

        if (text == null) {
            return "";
        }

        return text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
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


    // =========================================
    // ERROR PAGE
    // =========================================

    private void showError(
            PrintWriter out,
            String message) {

        out.println("""
                <!DOCTYPE html>
                <html>
                <head>
                    <title>Order Error</title>

                    <style>

                        body {
                            font-family: Arial, sans-serif;
                            background: #fff8f1;
                            text-align: center;
                            padding: 50px;
                        }

                        .box {
                            background: white;
                            max-width: 500px;
                            margin: auto;
                            padding: 30px;
                            border-radius: 15px;
                            box-shadow: 0 4px 15px rgba(0,0,0,0.1);
                        }

                        h1 {
                            color: #dc3545;
                        }

                        .btn {
                            display: inline-block;
                            margin-top: 20px;
                            padding: 12px 20px;
                            background: #ff6b00;
                            color: white;
                            text-decoration: none;
                            border-radius: 8px;
                        }

                    </style>
                </head>

                <body>

                    <div class="box">

                        <h1>❌ Order Failed</h1>

                        <p>
                """ + escapeHtml(message) + """
                        </p>

                        <a href="index.html"
                           class="btn">
                            ← Back to Home
                        </a>

                    </div>

                </body>
                </html>
                """);
    }
}