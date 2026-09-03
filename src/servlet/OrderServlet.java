import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Properties;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

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

        // =====================================================
        // GET ORDER DETAILS
        // =====================================================

        String customerName = request.getParameter("customerName");
        String foodName = request.getParameter("foodName");
        String quantityText = request.getParameter("quantity");

        // =====================================================
        // GET LOGGED-IN USER EMAIL
        // =====================================================

        HttpSession session = request.getSession(false);

        if (session == null ||
                session.getAttribute("email") == null) {

            showError(
                    out,
                    "User email not found. Please login again."
            );
            return;
        }

        String customerEmail =
                (String) session.getAttribute("email");

        // =====================================================
        // GET ADDRESS DETAILS
        // =====================================================

        String fullName = request.getParameter("fullName");
        String phone = request.getParameter("phone");
        String addressLine = request.getParameter("addressLine");
        String city = request.getParameter("city");
        String state = request.getParameter("state");
        String pincode = request.getParameter("pincode");

        // =====================================================
        // VALIDATION
        // =====================================================

        if (customerName == null ||
                customerName.trim().isEmpty() ||
                foodName == null ||
                foodName.trim().isEmpty() ||
                quantityText == null ||
                quantityText.trim().isEmpty() ||
                fullName == null ||
                fullName.trim().isEmpty() ||
                phone == null ||
                phone.trim().isEmpty() ||
                addressLine == null ||
                addressLine.trim().isEmpty() ||
                city == null ||
                city.trim().isEmpty() ||
                state == null ||
                state.trim().isEmpty() ||
                pincode == null ||
                pincode.trim().isEmpty()) {

            showError(
                    out,
                    "Please fill in all customer, order, and address details."
            );
            return;
        }

        // =====================================================
        // CONVERT QUANTITY
        // =====================================================

        int quantity;

        try {

            quantity = Integer.parseInt(quantityText);

            if (quantity < 1 || quantity > 20) {

                showError(
                        out,
                        "Quantity must be between 1 and 20."
                );
                return;
            }

        } catch (NumberFormatException e) {

            showError(
                    out,
                    "Please enter a valid quantity."
            );
            return;
        }

        // =====================================================
        // DATABASE ENVIRONMENT VARIABLES
        // =====================================================

        String url = System.getenv("DB_URL");
        String username = System.getenv("DB_USERNAME");
        String password = System.getenv("DB_PASSWORD");

        // =====================================================
        // CHECK DATABASE ENVIRONMENT VARIABLES
        // =====================================================

        if (url == null || url.isBlank()) {

            showError(
                    out,
                    "DB_URL is missing in Render Environment Variables."
            );
            return;
        }

        if (username == null || username.isBlank()) {

            showError(
                    out,
                    "DB_USERNAME is missing in Render Environment Variables."
            );
            return;
        }

        if (password == null || password.isBlank()) {

            showError(
                    out,
                    "DB_PASSWORD is missing in Render Environment Variables."
            );
            return;
        }

        // =====================================================
        // CONVERT MYSQL URL TO JDBC URL
        // =====================================================

        if (url.startsWith("mysql://")) {
            url = "jdbc:" + url;
        }

        // =====================================================
        // SQL QUERIES
        // =====================================================

        String orderSql = """
            INSERT INTO orders
            (customer_name, food_name, quantity)
            VALUES (?, ?, ?)
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

        // =====================================================
        // DATABASE OPERATION
        // =====================================================

        try {

            // Load MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connect to database
            try (
                Connection con =
                        DriverManager.getConnection(
                                url,
                                username,
                                password
                        );

                PreparedStatement orderPs =
                        con.prepareStatement(orderSql);

                PreparedStatement addressPs =
                        con.prepareStatement(addressSql)
            ) {

                // =================================================
                // SAVE ORDER
                // =================================================

                orderPs.setString(
                        1,
                        customerName.trim()
                );

                orderPs.setString(
                        2,
                        foodName.trim()
                );

                orderPs.setInt(
                        3,
                        quantity
                );

                orderPs.executeUpdate();

                System.out.println(
                        "ORDER STEP: Order saved successfully"
                );

                // =================================================
                // SAVE ADDRESS
                // =================================================

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

                System.out.println(
                        "ORDER STEP: Address saved successfully"
                );
            }

            // =====================================================
            // SEND CONFIRMATION EMAIL
            // =====================================================

            System.out.println(
                    "ORDER STEP: Starting email sending..."
            );


            System.out.println(
                    "ORDER STEP: Email method completed"
            );

            // =====================================================
            // SUCCESS PAGE
            // =====================================================

            out.println("""
                <!DOCTYPE html>

                <html lang="en">

                <head>

                    <meta charset="UTF-8">

                    <meta name="viewport"
                          content="width=device-width,
                                   initial-scale=1.0">

                    <title>
                        FoodieHub - Order Confirmed
                    </title>

                    <style>

                        * {
                            margin: 0;
                            padding: 0;
                            box-sizing: border-box;
                        }

                        body {
                            font-family: Arial, sans-serif;
                            background: #fff8f0;
                            min-height: 100vh;
                            display: flex;
                            justify-content: center;
                            align-items: center;
                            padding: 20px;
                        }

                        .success-card {
                            width: 100%;
                            max-width: 600px;
                            background: white;
                            padding: 45px 35px;
                            border-radius: 24px;
                            text-align: center;
                            box-shadow:
                                0 15px 40px
                                rgba(0,0,0,0.12);
                        }

                        .check {
                            width: 80px;
                            height: 80px;
                            margin: 0 auto 20px;
                            background: #ff6b00;
                            color: white;
                            border-radius: 50%;
                            display: flex;
                            align-items: center;
                            justify-content: center;
                            font-size: 42px;
                            font-weight: bold;
                        }

                        h1 {
                            color: #222;
                            font-size: 30px;
                            margin-bottom: 10px;
                        }

                        .subtitle {
                            color: #777;
                            margin-bottom: 30px;
                        }

                        .order-box {
                            background: #fff8f0;
                            border-radius: 15px;
                            padding: 20px;
                            margin-bottom: 28px;
                            text-align: left;
                        }

                        .row {
                            display: flex;
                            justify-content: space-between;
                            padding: 13px 0;
                            border-bottom: 1px solid #eee;
                            gap: 20px;
                        }

                        .row:last-child {
                            border-bottom: none;
                        }

                        .label {
                            color: #777;
                        }

                        .value {
                            color: #222;
                            font-weight: bold;
                            text-align: right;
                        }

                        .address {
                            margin-top: 20px;
                            padding-top: 20px;
                            border-top: 1px solid #eee;
                        }

                        .address-title {
                            font-size: 18px;
                            font-weight: bold;
                            margin-bottom: 12px;
                            color: #ff6b00;
                        }

                        .address-text {
                            color: #555;
                            line-height: 1.7;
                            text-align: right;
                        }

                        .home-btn {
                            display: inline-block;
                            background: #ff6b00;
                            color: white;
                            text-decoration: none;
                            padding: 14px 28px;
                            border-radius: 10px;
                            font-weight: bold;
                        }

                        .home-btn:hover {
                            background: #e85d00;
                        }

                        .brand {
                            margin-top: 25px;
                            color: #999;
                            font-size: 14px;
                        }

                        .brand span {
                            color: #ff6b00;
                            font-weight: bold;
                        }

                    </style>

                </head>

                <body>

                    <div class="success-card">

                        <div class="check">
                            ✓
                        </div>

                        <h1>
                            Order Placed Successfully!
                        </h1>

                        <p class="subtitle">
                            Thank you for ordering
                            with FoodieHub 🍴
                        </p>

                        <div class="order-box">

                            <div class="row">

                                <span class="label">
                                    Customer
                                </span>

                                <span class="value">
                """);

            out.println(
                    escapeHtml(customerName)
            );

            out.println("""
                                </span>

                            </div>

                            <div class="row">

                                <span class="label">
                                    Food
                                </span>

                                <span class="value">
                """);

            out.println(
                    escapeHtml(foodName)
            );

            out.println("""
                                </span>

                            </div>

                            <div class="row">

                                <span class="label">
                                    Quantity
                                </span>

                                <span class="value">
                """);

            out.println(quantity);

            out.println("""
                                </span>

                            </div>

                            <div class="address">

                                <div class="address-title">
                                    📍 Delivery Address
                                </div>

                                <div class="address-text">
                """);

            out.println(
                    escapeHtml(fullName)
            );

            out.println("<br>");

            out.println(
                    escapeHtml(phone)
            );

            out.println("<br>");

            out.println(
                    escapeHtml(addressLine)
            );

            out.println("<br>");

            out.println(
                    escapeHtml(city)
            );

            out.println(", ");

            out.println(
                    escapeHtml(state)
            );

            out.println("<br>");

            out.println(
                    escapeHtml(pincode)
            );

            out.println("""
                                </div>

                            </div>

                        </div>

                        <a href="index.html"
                           class="home-btn">

                            ← Back to Home

                        </a>

                        <p class="brand">

                            © 2026
                            <span>FoodieHub</span>

                        </p>

                    </div>

                </body>

                </html>
                """);

        } catch (Exception e) {

            e.printStackTrace();

            showError(
                    out,
                    e.getMessage()
            );
        }
    }

    // =========================================================
    // SEND ORDER EMAIL
    // =========================================================

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

        System.out.println(
                "MAIL STEP 1: entered sendOrderEmail"
        );

        // =====================================================
        // GET EMAIL ENVIRONMENT VARIABLES
        // =====================================================

        String senderEmail =
                System.getenv("EMAIL_USERNAME");

        String senderPassword =
                System.getenv("EMAIL_PASSWORD");

        if (senderEmail == null ||
                senderEmail.isBlank()) {

            throw new Exception(
                    "EMAIL_USERNAME is missing."
            );
        }

        if (senderPassword == null ||
                senderPassword.isBlank()) {

            throw new Exception(
                    "EMAIL_PASSWORD is missing."
            );
        }

        System.out.println(
                "MAIL STEP 2: Email environment variables found"
        );

        // =====================================================
        // GMAIL SMTP SETTINGS
        // =====================================================

        Properties props = new Properties();

        props.put(
                "mail.smtp.auth",
                "true"
        );

        props.put(
                "mail.smtp.starttls.enable",
                "true"
        );

        props.put(
                "mail.smtp.host",
                "smtp.gmail.com"
        );

        props.put(
                "mail.smtp.port",
                "587"
        );

        // Timeout settings so the request doesn't wait forever
        props.put(
                "mail.smtp.connectiontimeout",
                "10000"
        );

        props.put(
                "mail.smtp.timeout",
                "10000"
        );

        props.put(
                "mail.smtp.writetimeout",
                "10000"
        );

        System.out.println(
                "MAIL STEP 3: SMTP properties created"
        );

        // =====================================================
        // CREATE MAIL SESSION
        // =====================================================

        Session mailSession =
                Session.getInstance(
                        props,
                        new Authenticator() {

                            @Override
                            protected PasswordAuthentication
                            getPasswordAuthentication() {

                                return new PasswordAuthentication(
                                        senderEmail,
                                        senderPassword
                                );
                            }
                        }
                );

        System.out.println(
                "MAIL STEP 4: mail session created"
        );

        // =====================================================
        // CREATE EMAIL MESSAGE
        // =====================================================

        Message message =
                new MimeMessage(mailSession);

        System.out.println(
                "MAIL STEP 5: message created"
        );

        message.setFrom(
                new InternetAddress(senderEmail)
        );

        message.setRecipients(
                Message.RecipientType.TO,
                InternetAddress.parse(
                        customerEmail
                )
        );

        message.setSubject(
                "FoodieHub - Order Confirmed"
        );

        String emailBody =
                "Hello " + customerName + ",\n\n"

                + "Your FoodieHub order has been "
                + "placed successfully!\n\n"

                + "ORDER DETAILS\n"
                + "--------------------------\n"

                + "Food: " + foodName + "\n"

                + "Quantity: " + quantity + "\n\n"

                + "DELIVERY ADDRESS\n"
                + "--------------------------\n"

                + "Name: " + fullName + "\n"

                + "Phone: " + phone + "\n"

                + "Address: " + addressLine + "\n"

                + "City: " + city + "\n"

                + "State: " + state + "\n"

                + "Pincode: " + pincode + "\n\n"

                + "Thank you for ordering "
                + "with FoodieHub!\n";

        message.setText(emailBody);

        System.out.println(
                "MAIL STEP 6: about to send email"
        );

        // =====================================================
        // SEND EMAIL
        // =====================================================

        Transport.send(message);

        System.out.println(
                "MAIL STEP 7: EMAIL SENT SUCCESSFULLY"
        );
    }

    // =========================================================
    // ERROR PAGE
    // =========================================================

    private void showError(
            PrintWriter out,
            String message) {

        if (message == null ||
                message.isBlank()) {

            message =
                    "Unknown database error.";
        }

        out.println("""
            <!DOCTYPE html>

            <html lang="en">

            <head>

                <meta charset="UTF-8">

                <meta name="viewport"
                      content="width=device-width,
                               initial-scale=1.0">

                <title>
                    FoodieHub - Error
                </title>

                <style>

                    * {
                        box-sizing: border-box;
                    }

                    body {
                        font-family: Arial, sans-serif;
                        background: #fff8f0;
                        min-height: 100vh;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        padding: 20px;
                    }

                    .error-card {
                        background: white;
                        width: 100%;
                        max-width: 600px;
                        padding: 40px;
                        border-radius: 20px;
                        text-align: center;
                        box-shadow:
                            0 12px 35px
                            rgba(0,0,0,0.12);
                    }

                    h1 {
                        color: #ff6b00;
                        margin-bottom: 15px;
                    }

                    .error {
                        background: #fff3e8;
                        padding: 15px;
                        border-radius: 10px;
                        color: #555;
                        word-break: break-word;
                    }

                    a {
                        display: inline-block;
                        margin-top: 25px;
                        background: #ff6b00;
                        color: white;
                        text-decoration: none;
                        padding: 13px 25px;
                        border-radius: 10px;
                        font-weight: bold;
                    }

                </style>

            </head>

            <body>

                <div class="error-card">

                    <h1>
                        Something went wrong!
                    </h1>

                    <p class="error">
            """);

        out.println(
                escapeHtml(message)
        );

        out.println("""
                    </p>

                    <a href="order.html">
                        ← Back to Order
                    </a>

                </div>

            </body>

            </html>
            """);
    }

    // =========================================================
    // HTML ESCAPE
    // =========================================================

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




















































































