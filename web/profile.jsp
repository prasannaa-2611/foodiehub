<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.DriverManager" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>

<%
    // =========================================
    // CHECK LOGIN
    // =========================================

    Integer userId = (Integer) session.getAttribute("userId");

    if (userId == null) {
        response.sendRedirect("login.html");
        return;
    }

    // =========================================
    // GET USER DETAILS FROM SESSION
    // =========================================

    String fullName = (String) session.getAttribute("fullName");
    String email = (String) session.getAttribute("email");
    String phone = (String) session.getAttribute("phone");

    if (fullName == null) {
        fullName = "User";
    }

    if (email == null) {
        email = "";
    }

    if (phone == null) {
        phone = "";
    }

    // =========================================
    // DATABASE VARIABLES
    // =========================================

    String dbUrl = System.getenv("DB_URL");
    String dbUsername = System.getenv("DB_USERNAME");
    String dbPassword = System.getenv("DB_PASSWORD");

    if (dbUrl != null && dbUrl.startsWith("mysql://")) {
        dbUrl = "jdbc:" + dbUrl;
    }

    // =========================================
    // ADDRESS VARIABLES
    // =========================================

    String addressLine = "";
    String city = "";
    String state = "";
    String pincode = "";

    boolean addressExists = false;

    // =========================================
    // GET ADDRESS
    // =========================================

    if (dbUrl != null
            && dbUsername != null
            && dbPassword != null) {

        String sql =
                "SELECT address_line, city, state, pincode " +
                "FROM addresses " +
                "WHERE user_id = ? " +
                "ORDER BY id DESC " +
                "LIMIT 1";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (
                Connection con = DriverManager.getConnection(
                    dbUrl,
                    dbUsername,
                    dbPassword
                );

                PreparedStatement ps = con.prepareStatement(sql)
            ) {

                ps.setInt(1, userId);

                try (ResultSet rs = ps.executeQuery()) {

                    if (rs.next()) {

                        addressExists = true;

                        addressLine = rs.getString("address_line");
                        city = rs.getString("city");
                        state = rs.getString("state");
                        pincode = rs.getString("pincode");

                        if (addressLine == null) {
                            addressLine = "";
                        }

                        if (city == null) {
                            city = "";
                        }

                        if (state == null) {
                            state = "";
                        }

                        if (pincode == null) {
                            pincode = "";
                        }
                    }
                }
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    // =========================================
    // HTML ESCAPE
    // =========================================

    String safeName = fullName
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;");

    String safeEmail = email
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;");

    String safePhone = phone
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;");

    String safeAddress = addressLine
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;");

    String safeCity = city
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;");

    String safeState = state
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;");

    String safePincode = pincode
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;");
%>

<!DOCTYPE html>

<html lang="en">

<head>

    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>FoodieHub - Profile</title>

    <style>

        * {
            box-sizing: border-box;
            margin: 0;
            padding: 0;
        }

        body {
            font-family: Arial, sans-serif;
            background: #fff8f0;
            color: #222;
            min-height: 100vh;
        }

        /* NAVBAR */

        nav {
            height: 70px;
            background: white;
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 0 8%;
            box-shadow: 0 2px 10px rgba(0,0,0,0.08);
        }

        .logo {
            font-size: 26px;
            font-weight: bold;
        }

        .logo span {
            color: #ff6b00;
        }

        nav a {
            text-decoration: none;
            color: #333;
            margin-left: 20px;
            font-weight: bold;
        }

        nav a:hover {
            color: #ff6b00;
        }

        /* PAGE */

        .page {
            width: 90%;
            max-width: 750px;
            margin: 45px auto;
        }

        .card {
            background: white;
            border-radius: 20px;
            padding: 35px;
            margin-bottom: 25px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.1);
        }

        /* PROFILE HEADER */

        .profile-header {
            text-align: center;
            margin-bottom: 30px;
        }

        .avatar {
            width: 90px;
            height: 90px;
            border-radius: 50%;
            background: #ff6b00;
            color: white;
            margin: 0 auto 18px;

            display: flex;
            align-items: center;
            justify-content: center;

            font-size: 38px;
            font-weight: bold;
        }

        h1 {
            font-size: 30px;
            margin-bottom: 8px;
        }

        .welcome {
            color: #777;
        }

        /* SECTION */

        .section-title {
            font-size: 22px;
            margin-bottom: 20px;
        }

        .row {
            display: flex;
            justify-content: space-between;
            gap: 20px;
            padding: 15px 0;
            border-bottom: 1px solid #eee;
        }

        .row:last-child {
            border-bottom: none;
        }

        .label {
            color: #777;
            font-weight: bold;
        }

        .value {
            text-align: right;
            font-weight: bold;
            word-break: break-word;
        }

        /* ADDRESS */

        .address-box {
            background: #fff8f0;
            border-radius: 14px;
            padding: 20px;
            line-height: 1.7;
            margin-bottom: 20px;
        }

        .no-address {
            color: #777;
            text-align: center;
            padding: 15px;
        }

        /* BUTTONS */

        .buttons {
            display: flex;
            gap: 12px;
            flex-wrap: wrap;
        }

        .btn {
            display: inline-block;
            text-decoration: none;
            padding: 13px 22px;
            border-radius: 10px;
            font-weight: bold;
            text-align: center;
        }

        .primary {
            background: #ff6b00;
            color: white;
        }

        .primary:hover {
            background: #e85d00;
        }

        .secondary {
            border: 2px solid #ff6b00;
            color: #ff6b00;
            background: white;
        }

        .secondary:hover {
            background: #fff1e4;
        }

        /* FOOTER */

        footer {
            text-align: center;
            color: #777;
            padding: 25px;
        }

        /* MOBILE */

        @media (max-width: 600px) {

            nav {
                height: auto;
                padding: 20px;
                flex-direction: column;
                gap: 15px;
            }

            nav a {
                margin: 0 7px;
            }

            .page {
                margin: 25px auto;
            }

            .card {
                padding: 25px 20px;
            }

            .row {
                flex-direction: column;
                gap: 5px;
            }

            .value {
                text-align: left;
            }

            .buttons {
                flex-direction: column;
            }

            .btn {
                width: 100%;
            }
        }

    </style>

</head>

<body>

    <!-- NAVIGATION -->

    <nav>

        <div class="logo">
            Foodie<span>Hub</span>
        </div>

        <div>
            <a href="index.html">Home</a>
            <a href="order.html">Order</a>
        </div>

    </nav>


    <!-- PROFILE -->

    <div class="page">

        <div class="card">

            <div class="profile-header">

                <div class="avatar">
                    <%= safeName.substring(0, 1).toUpperCase() %>
                </div>

                <h1>
                    <%= safeName %>
                </h1>

                <p class="welcome">
                    Welcome to your FoodieHub profile 👋
                </p>

            </div>


            <h2 class="section-title">
                Personal Information
            </h2>


            <div class="row">

                <span class="label">
                    Name
                </span>

                <span class="value">
                    <%= safeName %>
                </span>

            </div>


            <div class="row">

                <span class="label">
                    Email
                </span>

                <span class="value">
                    <%= safeEmail %>
                </span>

            </div>


            <div class="row">

                <span class="label">
                    Phone
                </span>

                <span class="value">
                    <%= safePhone %>
                </span>

            </div>

        </div>


        <!-- ADDRESS CARD -->

        <div class="card">

            <h2 class="section-title">
                📍 Delivery Address
            </h2>

            <% if (addressExists) { %>

                <div class="address-box">

                    <strong>
                        <%= safeAddress %>
                    </strong>

                    <br>

                    <%= safeCity %>,
                    <%= safeState %>
                    -
                    <%= safePincode %>

                </div>

                <div class="buttons">

                    <a href="address.html"
                       class="btn primary">
                        Edit Address
                    </a>

                </div>

            <% } else { %>

                <div class="no-address">

                    <p>
                        You haven't added a delivery address yet.
                    </p>

                </div>

                <div class="buttons">

                    <a href="address.html"
                       class="btn primary">
                        + Add Address
                    </a>

                </div>

            <% } %>

        </div>


        <!-- ACTIONS -->

        <div class="card">

            <div class="buttons">

                <a href="index.html"
                   class="btn secondary">
                    ← Back Home
                </a>

                <a href="order.html"
                   class="btn primary">
                    🍴 Order Food
                </a>

            </div>

        </div>

    </div>


    <footer>

        © 2026 FoodieHub • Delicious food, happy moments ❤️

    </footer>

</body>

</html>