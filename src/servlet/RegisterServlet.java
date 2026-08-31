import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        PrintWriter out = response.getWriter();


        // =========================================
        // GET FORM VALUES
        // =========================================

        String fullName =
                request.getParameter("fullName");

        String email =
                request.getParameter("email");

        String phone =
                request.getParameter("phone");

        String password =
                request.getParameter("password");


        // =========================================
        // VALIDATION
        // =========================================

        if (fullName == null || fullName.trim().isEmpty()
                || email == null || email.trim().isEmpty()
                || phone == null || phone.trim().isEmpty()
                || password == null || password.isEmpty()) {

            showMessage(
                    out,
                    "Please fill in all registration details."
            );

            return;
        }


        // =========================================
        // DATABASE ENVIRONMENT VARIABLES
        // =========================================

        String url =
                System.getenv("DB_URL");

        String username =
                System.getenv("DB_USERNAME");

        String dbPassword =
                System.getenv("DB_PASSWORD");


        if (url == null || url.isBlank()) {

            showMessage(
                    out,
                    "DB_URL is missing."
            );

            return;
        }


        if (username == null || username.isBlank()) {

            showMessage(
                    out,
                    "DB_USERNAME is missing."
            );

            return;
        }


        if (dbPassword == null || dbPassword.isBlank()) {

            showMessage(
                    out,
                    "DB_PASSWORD is missing."
            );

            return;
        }


        // =========================================
        // JDBC URL
        // =========================================

        if (url.startsWith("mysql://")) {

            url = "jdbc:" + url;
        }


        // =========================================
        // HASH PASSWORD
        // =========================================

        String passwordHash;

        try {

            passwordHash =
                    hashPassword(password);

        } catch (Exception e) {

            showMessage(
                    out,
                    "Unable to process password."
            );

            return;
        }


        // =========================================
        // INSERT USER
        // =========================================

        String sql = """
            INSERT INTO users
            (full_name, email, phone, password_hash)
            VALUES (?, ?, ?, ?)
            """;


        try {

            Class.forName(
                    "com.mysql.cj.jdbc.Driver"
            );


            try (
                Connection con =
                        DriverManager.getConnection(
                                url,
                                username,
                                dbPassword
                        );

                PreparedStatement ps =
                        con.prepareStatement(sql)
            ) {

                ps.setString(
                        1,
                        fullName.trim()
                );

                ps.setString(
                        2,
                        email.trim()
                );

                ps.setString(
                        3,
                        phone.trim()
                );

                ps.setString(
                        4,
                        passwordHash
                );


                ps.executeUpdate();
            }


            // =====================================
            // SUCCESS
            // =====================================

            out.println("""
                <!DOCTYPE html>

                <html>

                <head>

                    <title>
                        FoodieHub - Account Created
                    </title>

                    <style>

                        body {
                            font-family: Arial;
                            background: #fff8f0;

                            min-height: 100vh;

                            display: flex;
                            align-items: center;
                            justify-content: center;
                        }

                        .card {
                            background: white;

                            padding: 40px;

                            border-radius: 20px;

                            text-align: center;

                            box-shadow:
                                0 10px 30px
                                rgba(0,0,0,0.1);
                        }

                        h1 {
                            color: #ff6b00;
                            margin-bottom: 15px;
                        }

                        a {
                            display: inline-block;

                            margin-top: 20px;

                            padding: 12px 25px;

                            background: #ff6b00;

                            color: white;

                            text-decoration: none;

                            border-radius: 10px;
                        }

                    </style>

                </head>

                <body>

                    <div class="card">

                        <h1>
                            Account Created! 🎉
                        </h1>

                        <p>
                            Welcome to FoodieHub.
                        </p>

                        <a href="login.html">
                            Login Now
                        </a>

                    </div>

                </body>

                </html>
                """);


        } catch (Exception e) {

            e.printStackTrace();

            showMessage(
                    out,
                    "Registration failed. The email may already be registered."
            );
        }
    }


    // =============================================
    // PASSWORD HASHING
    // =============================================

    private String hashPassword(
            String password)
            throws Exception {

        MessageDigest digest =
                MessageDigest.getInstance("SHA-256");

        byte[] hash =
                digest.digest(
                        password.getBytes("UTF-8")
                );

        StringBuilder hex =
                new StringBuilder();

        for (byte b : hash) {

            String h =
                    Integer.toHexString(
                            0xff & b
                    );

            if (h.length() == 1) {

                hex.append('0');
            }

            hex.append(h);
        }

        return hex.toString();
    }


    // =============================================
    // ERROR PAGE
    // =============================================

    private void showMessage(
            PrintWriter out,
            String message) {

        out.println("""
            <!DOCTYPE html>

            <html>

            <head>

                <title>
                    FoodieHub
                </title>

                <style>

                    body {
                        font-family: Arial;
                        background: #fff8f0;

                        min-height: 100vh;

                        display: flex;
                        align-items: center;
                        justify-content: center;
                    }

                    .card {
                        background: white;

                        padding: 40px;

                        border-radius: 20px;

                        text-align: center;

                        max-width: 500px;

                        box-shadow:
                            0 10px 30px
                            rgba(0,0,0,0.1);
                    }

                    h1 {
                        color: #ff6b00;
                    }

                    .message {
                        color: #555;

                        margin-top: 15px;
                    }

                    a {
                        display: inline-block;

                        margin-top: 20px;

                        padding: 12px 25px;

                        background: #ff6b00;

                        color: white;

                        text-decoration: none;

                        border-radius: 10px;
                    }

                </style>

            </head>

            <body>

                <div class="card">

                    <h1>
                        Something went wrong
                    </h1>

                    <p class="message">
            """);

        out.println(
                escapeHtml(message)
        );

        out.println("""
                    </p>

                    <a href="register.html">
                        ← Back to Register
                    </a>

                </div>

            </body>

            </html>
            """);
    }


    // =============================================
    // HTML ESCAPE
    // =============================================

    private String escapeHtml(
            String text) {

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