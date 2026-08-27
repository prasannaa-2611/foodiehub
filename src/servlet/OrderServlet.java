import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/OrderServlet")
public class OrderServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        PrintWriter out = response.getWriter();

        // Get form values
        String name = request.getParameter("customerName");
        String food = request.getParameter("foodName");
        String quantityText = request.getParameter("quantity");

        // Basic validation
        if (name == null || name.trim().isEmpty()
                || food == null || food.trim().isEmpty()
                || quantityText == null || quantityText.trim().isEmpty()) {

            out.println("""
                <!DOCTYPE html>
                <html>
                <head>
                    <title>FoodieHub - Error</title>
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            background: #fff8f0;
                            text-align: center;
                            padding: 80px 20px;
                        }

                        .box {
                            background: white;
                            max-width: 500px;
                            margin: auto;
                            padding: 40px;
                            border-radius: 20px;
                            box-shadow: 0 10px 30px rgba(0,0,0,0.1);
                        }

                        h1 {
                            color: #ff6b00;
                        }

                        a {
                            display: inline-block;
                            margin-top: 20px;
                            padding: 12px 25px;
                            background: #ff6b00;
                            color: white;
                            text-decoration: none;
                            border-radius: 8px;
                        }
                    </style>
                </head>

                <body>
                    <div class="box">
                        <h1>Invalid Order</h1>
                        <p>Please fill in all the order details.</p>
                        <a href="order.html">← Back to Order</a>
                    </div>
                </body>
                </html>
            """);

            return;
        }

        int quantity;

        try {
            quantity = Integer.parseInt(quantityText);
        } catch (NumberFormatException e) {

            out.println("""
                <!DOCTYPE html>
                <html>
                <head>
                    <title>FoodieHub - Error</title>
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            background: #fff8f0;
                            text-align: center;
                            padding: 80px 20px;
                        }

                        .box {
                            background: white;
                            max-width: 500px;
                            margin: auto;
                            padding: 40px;
                            border-radius: 20px;
                            box-shadow: 0 10px 30px rgba(0,0,0,0.1);
                        }

                        h1 {
                            color: #ff6b00;
                        }

                        a {
                            display: inline-block;
                            margin-top: 20px;
                            padding: 12px 25px;
                            background: #ff6b00;
                            color: white;
                            text-decoration: none;
                            border-radius: 8px;
                        }
                    </style>
                </head>

                <body>
                    <div class="box">
                        <h1>Invalid Quantity</h1>
                        <p>Please enter a valid quantity.</p>
                        <a href="order.html">← Back to Order</a>
                    </div>
                </body>
                </html>
            """);

            return;
        }

        // Database environment variables
        String url = System.getenv("DB_URL");
        String username = System.getenv("DB_USERNAME");
        String password = System.getenv("DB_PASSWORD");

        // Check environment variables
        if (url == null || url.isBlank()) {
            showError(out, "DB_URL is missing in Render Environment Variables.");
            return;
        }

        if (username == null || username.isBlank()) {
            showError(out, "DB_USERNAME is missing in Render Environment Variables.");
            return;
        }

        if (password == null || password.isBlank()) {
            showError(out, "DB_PASSWORD is missing in Render Environment Variables.");
            return;
        }

        // Convert Aiven mysql:// URL to JDBC URL if necessary
        if (url.startsWith("mysql://")) {
            url = "jdbc:" + url;
        }

        // SQL query
        String sql = """
            INSERT INTO orders
            (customer_name, food_name, quantity)
            VALUES (?, ?, ?)
            """;

        try {

            // Load MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connect to database
            try (Connection con = DriverManager.getConnection(
                    url,
                    username,
                    password
            );

            PreparedStatement ps = con.prepareStatement(sql)) {

                // Set values
                ps.setString(1, name.trim());
                ps.setString(2, food.trim());
                ps.setInt(3, quantity);

                // Insert order
                ps.executeUpdate();
            }

            // SUCCESS PAGE
            out.println("""
                <!DOCTYPE html>
                <html lang="en">

                <head>

                    <meta charset="UTF-8">
                    <meta name="viewport"
                          content="width=device-width, initial-scale=1.0">

                    <title>FoodieHub - Order Confirmed</title>

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
                            max-width: 550px;
                            background: white;
                            padding: 45px 35px;
                            border-radius: 24px;
                            text-align: center;
                            box-shadow:
                                0 15px 40px rgba(0,0,0,0.12);
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

                        <div class="check">✓</div>

                        <h1>Order Placed Successfully!</h1>

                        <p class="subtitle">
                            Thank you for ordering with FoodieHub 🍴
                        </p>

                        <div class="order-box">

                            <div class="row">
                                <span class="label">Customer</span>
                                <span class="value">
            """);

            out.println(escapeHtml(name));

            out.println("""
                                </span>
                            </div>

                            <div class="row">
                                <span class="label">Food</span>
                                <span class="value">
            """);

            out.println(escapeHtml(food));

            out.println("""
                                </span>
                            </div>

                            <div class="row">
                                <span class="label">Quantity</span>
                                <span class="value">
            """);

            out.println(quantity);

            out.println("""
                                </span>
                            </div>

                        </div>

                        <a href="index.html" class="home-btn">
                            ← Back to Home
                        </a>

                        <p class="brand">
                            © 2026 <span>FoodieHub</span>
                        </p>

                    </div>

                </body>

                </html>
            """);

        } catch (Exception e) {

            e.printStackTrace();

            showError(out, e.getMessage());
        }
    }

    // Error page
    private void showError(PrintWriter out, String message) {

        if (message == null || message.isBlank()) {
            message = "Unknown database error.";
        }

        out.println("""
            <!DOCTYPE html>
            <html lang="en">

            <head>

                <meta charset="UTF-8">

                <meta name="viewport"
                      content="width=device-width, initial-scale=1.0">

                <title>FoodieHub - Error</title>

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
                        box-shadow: 0 12px 35px rgba(0,0,0,0.12);
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

                    <h1>Something went wrong!</h1>

                    <p class="error">
            """);

        out.println(escapeHtml(message));

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

    // Prevent HTML special characters from being displayed as HTML
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