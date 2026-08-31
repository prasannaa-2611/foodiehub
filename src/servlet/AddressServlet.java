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

@WebServlet("/AddressServlet")
public class AddressServlet extends HttpServlet {

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        // Check login
        if (session == null ||
                session.getAttribute("userId") == null) {

            response.sendRedirect("login.html");
            return;
        }

        int userId = (Integer) session.getAttribute("userId");

        String url = System.getenv("DB_URL");
        String username = System.getenv("DB_USERNAME");
        String password = System.getenv("DB_PASSWORD");

        if (url != null && url.startsWith("mysql://")) {
            url = "jdbc:" + url;
        }

        String sql =
                "SELECT full_name, phone, address_line, " +
                "city, state, pincode " +
                "FROM addresses " +
                "WHERE user_id = ? " +
                "ORDER BY id DESC " +
                "LIMIT 1";

        try {

            Class.forName("com.mysql.cj.jdbc.Driver");

            try (
                Connection con = DriverManager.getConnection(
                    url,
                    username,
                    password
                );

                PreparedStatement ps = con.prepareStatement(sql)
            ) {

                ps.setInt(1, userId);

                try (ResultSet rs = ps.executeQuery()) {

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
                    }

                    request.getRequestDispatcher(
                            "address.jsp"
                    ).forward(request, response);
                }
            }

        } catch (Exception e) {

            e.printStackTrace();

            response.sendError(
                    500,
                    "Unable to load address."
            );
        }
    }


    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        // Check login
        if (session == null ||
                session.getAttribute("userId") == null) {

            response.sendRedirect("login.html");
            return;
        }

        int userId = (Integer) session.getAttribute("userId");

        // Get form values
        String fullName = request.getParameter("fullName");
        String phone = request.getParameter("phone");
        String addressLine = request.getParameter("addressLine");
        String city = request.getParameter("city");
        String state = request.getParameter("state");
        String pincode = request.getParameter("pincode");

        // Validation
        if (fullName == null || fullName.isBlank()
                || phone == null || phone.isBlank()
                || addressLine == null || addressLine.isBlank()
                || city == null || city.isBlank()
                || state == null || state.isBlank()
                || pincode == null || pincode.isBlank()) {

            response.sendRedirect("AddressServlet");
            return;
        }

        // Database variables
        String url = System.getenv("DB_URL");
        String username = System.getenv("DB_USERNAME");
        String password = System.getenv("DB_PASSWORD");

        if (url == null || username == null || password == null) {

            response.sendError(
                    500,
                    "Database environment variables are missing."
            );

            return;
        }

        if (url.startsWith("mysql://")) {
            url = "jdbc:" + url;
        }

        try {

            Class.forName("com.mysql.cj.jdbc.Driver");

            try (
                Connection con = DriverManager.getConnection(
                    url,
                    username,
                    password
                )
            ) {

                // Check existing address
                String checkSql =
                        "SELECT id " +
                        "FROM addresses " +
                        "WHERE user_id = ? " +
                        "LIMIT 1";

                int addressId = -1;

                try (
                    PreparedStatement ps =
                            con.prepareStatement(checkSql)
                ) {

                    ps.setInt(1, userId);

                    try (ResultSet rs = ps.executeQuery()) {

                        if (rs.next()) {
                            addressId = rs.getInt("id");
                        }
                    }
                }


                // UPDATE EXISTING ADDRESS
                if (addressId != -1) {

                    String updateSql =
                            "UPDATE addresses SET " +
                            "customer_name = ?, " +
                            "full_name = ?, " +
                            "phone = ?, " +
                            "address_line = ?, " +
                            "city = ?, " +
                            "state = ?, " +
                            "pincode = ?, " +
                            "is_default = TRUE " +
                            "WHERE id = ?";

                    try (
                        PreparedStatement ps =
                                con.prepareStatement(updateSql)
                    ) {

                        ps.setString(1, fullName);
                        ps.setString(2, fullName);
                        ps.setString(3, phone);
                        ps.setString(4, addressLine);
                        ps.setString(5, city);
                        ps.setString(6, state);
                        ps.setString(7, pincode);
                        ps.setInt(8, addressId);

                        ps.executeUpdate();
                    }

                }

                // INSERT NEW ADDRESS
                else {

                    String insertSql =
                            "INSERT INTO addresses " +
                            "(user_id, customer_name, full_name, " +
                            "phone, address_line, city, state, " +
                            "pincode, is_default) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, TRUE)";

                    try (
                        PreparedStatement ps =
                                con.prepareStatement(insertSql)
                    ) {

                        ps.setInt(1, userId);
                        ps.setString(2, fullName);
                        ps.setString(3, fullName);
                        ps.setString(4, phone);
                        ps.setString(5, addressLine);
                        ps.setString(6, city);
                        ps.setString(7, state);
                        ps.setString(8, pincode);

                        ps.executeUpdate();
                    }
                }
            }

            // Go back to profile
            response.sendRedirect("profile.jsp");

        } catch (Exception e) {

            e.printStackTrace();

            response.sendError(
                    500,
                    "Unable to save address: " + e.getMessage()
            );
        }
    }
}