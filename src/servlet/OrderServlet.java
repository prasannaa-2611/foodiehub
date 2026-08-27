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

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String name = request.getParameter("customerName");
        String food = request.getParameter("foodName");
        int quantity = Integer.parseInt(request.getParameter("quantity"));

        String url = System.getenv("DB_URL");
String username = System.getenv("DB_USERNAME");
String password = System.getenv("DB_PASSWORD");

        String sql = "INSERT INTO orders (customer_name, food_name, quantity) VALUES (?, ?, ?)";

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {

            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection con = DriverManager.getConnection(
                    url, username, password);

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, name);
            ps.setString(2, food);
            ps.setInt(3, quantity);

            ps.executeUpdate();

            out.println("""
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

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
            align-items: center;
            justify-content: center;
            padding: 20px;
        }

        .success-card {
            background: white;
            width: 100%;
            max-width: 520px;
            padding: 45px 35px;
            text-align: center;
            border-radius: 22px;
            box-shadow: 0 12px 35px rgba(0, 0, 0, 0.12);
        }

        .check {
            width: 75px;
            height: 75px;
            margin: 0 auto 20px;
            border-radius: 50%;
            background: #ff6b00;
            color: white;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 40px;
            font-weight: bold;
        }

        h1 {
            font-size: 30px;
            margin-bottom: 10px;
            color: #222;
        }

        .subtitle {
            color: #777;
            margin-bottom: 30px;
            font-size: 16px;
        }

        .order-box {
            background: #fff8f0;
            border-radius: 14px;
            padding: 20px;
            text-align: left;
            margin-bottom: 28px;
        }

        .row {
            display: flex;
            justify-content: space-between;
            padding: 12px 0;
            border-bottom: 1px solid #eee;
        }

        .row:last-child {
            border-bottom: none;
        }

        .label {
            color: #777;
        }

        .value {
            font-weight: bold;
            color: #222;
        }

        .home-btn {
            display: inline-block;
            text-decoration: none;
            background: #ff6b00;
            color: white;
            padding: 14px 28px;
            border-radius: 10px;
            font-weight: bold;
            transition: 0.2s;
        }

        .home-btn:hover {
            background: #e85d00;
            transform: translateY(-2px);
        }

        .brand {
            margin-top: 25px;
            font-size: 14px;
            color: #999;
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
                <span class="value">%s</span>
            </div>

            <div class="row">
                <span class="label">Food</span>
                <span class="value">%s</span>
            </div>

            <div class="row">
                <span class="label">Quantity</span>
                <span class="value">%d</span>
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
""".formatted(name, food, quantity));
            ps.close();
            con.close();

        } catch (Exception e) {

            out.println("<h1>Something went wrong!</h1>");
            out.println("<p>" + e.getMessage() + "</p>");
        }
    }
}