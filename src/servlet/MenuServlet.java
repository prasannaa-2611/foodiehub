import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/MenuServlet")
public class MenuServlet extends HttpServlet {

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        PrintWriter out = response.getWriter();

        // =========================================
        // DATABASE DETAILS
        // =========================================

        String url = System.getenv("DB_URL");
        String username = System.getenv("DB_USERNAME");
        String password = System.getenv("DB_PASSWORD");

        if (url == null || username == null || password == null) {

            out.println(
                "<h2>Database environment variables are missing.</h2>"
            );

            return;
        }

        // Convert Render MySQL URL to JDBC URL

        if (url.startsWith("mysql://")) {
            url = "jdbc:" + url;
        }


        // =========================================
        // HTML START
        // =========================================

        out.println("""
                <!DOCTYPE html>
                <html>

                <head>

                    <meta charset="UTF-8">

                    <meta name="viewport"
                          content="width=device-width, initial-scale=1.0">

                    <title>FoodieHub Menu</title>

                    <style>

                        body {
                            font-family: Arial, sans-serif;
                            background-color: #f7f7f7;
                            margin: 0;
                            padding: 30px;
                        }

                        .top-bar {
                            max-width: 1100px;
                            margin: 0 auto 25px auto;

                            display: flex;
                            justify-content: space-between;
                            align-items: center;
                        }

                        h1 {
                            text-align: center;
                            color: #ff6b00;
                            margin-bottom: 30px;
                        }

                        .cart-btn {
                            background-color: #ff6b00;
                            color: white;

                            padding: 11px 18px;

                            border-radius: 8px;

                            text-decoration: none;

                            font-weight: bold;
                        }

                        .cart-btn:hover {
                            background-color: #e65c00;
                        }

                        .menu {
                            display: grid;

                            grid-template-columns:
                                repeat(auto-fit, minmax(220px, 1fr));

                            gap: 25px;

                            max-width: 1100px;

                            margin: 0 auto;
                        }

                        .food-card {
                            background: white;

                            padding: 20px;

                            border-radius: 15px;

                            box-shadow:
                                0 4px 12px rgba(0,0,0,0.1);
                        }

                        .food-card h2 {
                            margin-bottom: 8px;
                        }

                        .category {
                            color: #777;
                            font-size: 14px;
                        }

                        .price {
                            font-size: 20px;

                            font-weight: bold;

                            color: #ff6b00;

                            margin: 12px 0;
                        }

                        .description {
                            color: #555;

                            min-height: 45px;
                        }

                        .cart-add-btn {
                            display: inline-block;

                            background-color: #ff6b00;

                            color: white;

                            padding: 10px 18px;

                            border-radius: 8px;

                            text-decoration: none;

                            margin-top: 10px;
                        }

                        .cart-add-btn:hover {
                            background-color: #e65c00;
                        }

                    </style>

                </head>

                <body>

                    <div class="top-bar">

                        <div></div>

                        <a class="cart-btn"
                           href="cart.jsp">
                            🛒 View Cart
                        </a>

                    </div>

                    <h1>FoodieHub Menu</h1>

                    <div class="menu">
                """);


        // =========================================
        // GET FOOD FROM DATABASE
        // =========================================

        String sql = """
                SELECT id, name, category, price, description
                FROM foods
                WHERE available = TRUE
                ORDER BY id
                """;


        try (

                Connection con =
                        DriverManager.getConnection(
                                url,
                                username,
                                password
                        );

                PreparedStatement ps =
                        con.prepareStatement(sql);

                ResultSet rs =
                        ps.executeQuery()

        ) {

            while (rs.next()) {

                int id =
                        rs.getInt("id");

                String name =
                        rs.getString("name");

                String category =
                        rs.getString("category");

                BigDecimal price =
                        rs.getBigDecimal("price");

                String description =
                        rs.getString("description");


                // =========================================
                // FOOD CARD
                // =========================================

                out.println(
                    "<div class='food-card'>"
                );


                out.println(
                    "<h2>" +
                    escapeHtml(name) +
                    "</h2>"
                );


                out.println(
                    "<div class='category'>" +
                    escapeHtml(category) +
                    "</div>"
                );


                out.println(
                    "<div class='price'>₹" +
                    price +
                    "</div>"
                );


                out.println(
                    "<div class='description'>" +
                    escapeHtml(description) +
                    "</div>"
                );


                // =========================================
                // ADD TO CART
                // =========================================

                out.println(
                    "<a class='cart-add-btn' " +

                    "href='AddToCartServlet?foodId=" +

                    id +

                    "'>" +

                    "🛒 Add to Cart" +

                    "</a>"
                );


                out.println(
                    "</div>"
                );
            }


        } catch (Exception e) {

            out.println(
                "<h2>Unable to load menu.</h2>"
            );

            out.println(
                "<p>" +
                escapeHtml(e.getMessage()) +
                "</p>"
            );
        }


        // =========================================
        // HTML END
        // =========================================

        out.println("""
                    </div>

                </body>

                </html>
                """);
    }


    // =========================================
    // HTML ESCAPE
    // =========================================

    private String escapeHtml(String value) {

        if (value == null) {
            return "";
        }

        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}