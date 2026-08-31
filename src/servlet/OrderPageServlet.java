import java.io.IOException;
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

@WebServlet("/OrderPageServlet")
public class OrderPageServlet extends HttpServlet {

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        // =========================================
        // CHECK LOGIN
        // =========================================

        HttpSession session = request.getSession(false);

        if (session == null ||
            session.getAttribute("userId") == null) {

            response.sendRedirect("login.html");
            return;
        }

        int userId =
                (Integer) session.getAttribute("userId");

String selectedFood = request.getParameter("food");

if (selectedFood == null) {
    selectedFood = "";
}

request.setAttribute("selectedFood", selectedFood);
        // =========================================
        // DATABASE VARIABLES
        // =========================================

        String url =
                System.getenv("DB_URL");

        String username =
                System.getenv("DB_USERNAME");

        String password =
                System.getenv("DB_PASSWORD");

        if (url != null && url.startsWith("mysql://")) {
            url = "jdbc:" + url;
        }


        // =========================================
        // GET SAVED ADDRESS
        // =========================================

        String sql =
                "SELECT full_name, phone, address_line, " +
                "city, state, pincode " +
                "FROM addresses " +
                "WHERE user_id = ? " +
                "ORDER BY id DESC " +
                "LIMIT 1";


        try {

            Class.forName(
                    "com.mysql.cj.jdbc.Driver"
            );


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

                ps.setInt(1, userId);


                try (ResultSet rs =
                        ps.executeQuery()) {

                    // =================================
                    // ADDRESS FOUND
                    // =================================

                    if (rs.next()) {

                        request.setAttribute(
                                "fullName",
                                rs.getString("full_name")
                        );

                        request.setAttribute(
                                "phone",
                                rs.getString("phone")
                        );

                        request.setAttribute(
                                "addressLine",
                                rs.getString("address_line")
                        );

                        request.setAttribute(
                                "city",
                                rs.getString("city")
                        );

                        request.setAttribute(
                                "state",
                                rs.getString("state")
                        );

                        request.setAttribute(
                                "pincode",
                                rs.getString("pincode")
                        );

                        request.setAttribute(
                                "addressExists",
                                true
                        );

                    } else {

                        // No address saved yet

                        request.setAttribute(
                                "addressExists",
                                false
                        );
                    }


                    // =================================
                    // OPEN ORDER PAGE
                    // =================================

                    request.getRequestDispatcher(
                            "order.jsp"
                    ).forward(
                            request,
                            response
                    );
                }
            }


        } catch (Exception e) {

            e.printStackTrace();

            response.sendError(
                    500,
                    "Unable to load order page."
            );
        }
    }
}