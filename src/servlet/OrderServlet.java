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

        String url = "jdbc:mysql://localhost:3306/foodiehub";
        String username = "root";
        String password = "Foodie@2026";

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

            out.println("<h1>Order Placed Successfully!</h1>");
            out.println("<p>Hello " + name + "!</p>");
            out.println("<p>Food: " + food + "</p>");
            out.println("<p>Quantity: " + quantity + "</p>");

            ps.close();
            con.close();

        } catch (Exception e) {

            out.println("<h1>Something went wrong!</h1>");
            out.println("<p>" + e.getMessage() + "</p>");
        }
    }
}