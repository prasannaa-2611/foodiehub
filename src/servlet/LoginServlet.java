import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
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

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

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

        String email =
                request.getParameter("email");

        String password =
                request.getParameter("password");


        // =========================================
        // VALIDATION
        // =========================================

        if (email == null || email.trim().isEmpty()
                || password == null || password.isEmpty()) {

            showMessage(
                    out,
                    "Please enter your email and password."
            );

            return;
        }


        // =========================================
        // DATABASE VARIABLES
        // =========================================

        String url =
                System.getenv("DB_URL");

        String username =
                System.getenv("DB_USERNAME");

        String dbPassword =
                System.getenv("DB_PASSWORD");


        if (url == null || url.isBlank()
                || username == null || username.isBlank()
                || dbPassword == null || dbPassword.isBlank()) {

            showMessage(
                    out,
                    "Database environment variables are missing."
            );

            return;
        }


        if (url.startsWith("mysql://")) {
            url = "jdbc:" + url;
        }


        // =========================================
        // HASH ENTERED PASSWORD
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
        // FIND USER
        // =========================================

        String sql = """
            SELECT id, full_name, email, phone
            FROM users
            WHERE email = ?
            AND password_hash = ?
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
                        email.trim()
                );

                ps.setString(
                        2,
                        passwordHash
                );


                try (ResultSet rs =
                        ps.executeQuery()) {


                    if (rs.next()) {

                        // =================================
                        // LOGIN SUCCESS
                        // =================================

                        int userId =
                                rs.getInt("id");

                        String fullName =
                                rs.getString("full_name");

                        String userEmail =
                                rs.getString("email");

                        String phone =
                                rs.getString("phone");


                        // Create session
                        HttpSession session =
                                request.getSession();

                        session.setAttribute(
                                "userId",
                                userId
                        );

                        session.setAttribute(
                                "fullName",
                                fullName
                        );

                        session.setAttribute(
                                "email",
                                userEmail
                        );

                        session.setAttribute(
                                "phone",
                                phone
                        );


                        // Go to profile
                        response.sendRedirect(
                                "profile.jsp"
                        );

                        return;

                    } else {

                        showMessage(
                                out,
                                "Invalid email or password."
                        );

                        return;
                    }
                }
            }


        } catch (Exception e) {

            e.printStackTrace();

            showMessage(
                    out,
                    "Login failed. Please try again."
            );
        }
    }


    // =============================================
    // PASSWORD HASH
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

                <title>FoodieHub - Login</title>

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

                    p {
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
                        Login Failed
                    </h1>

                    <p>
            """);

        out.println(
                escapeHtml(message)
        );

        out.println("""
                    </p>

                    <a href="login.html">
                        ← Back to Login
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