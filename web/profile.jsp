<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.DriverManager" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="jakarta.servlet.http.HttpSession" %>

<%
    // =========================================
    // CHECK LOGIN
    // =========================================

    HttpSession userSession = request.getSession(false);

    if (userSession == null ||
        userSession.getAttribute("userId") == null) {

        response.sendRedirect("login.html");
        return;
    }

    int userId = (Integer) userSession.getAttribute("userId");

    String fullName =
            (String) userSession.getAttribute("fullName");

    String email =
            (String) userSession.getAttribute("email");

    String phone =
            (String) userSession.getAttribute("phone");


    // =========================================
    // ADDRESS VARIABLES
    // =========================================

    String addressLine = null;
    String city = null;
    String state = null;
    String pincode = null;

    boolean hasAddress = false;


    // =========================================
    // DATABASE CONNECTION
    // =========================================

    String url = System.getenv("DB_URL");
    String username = System.getenv("DB_USERNAME");
    String password = System.getenv("DB_PASSWORD");

    if (url != null && url.startsWith("mysql://")) {
        url = "jdbc:" + url;
    }


    // =========================================
    // GET USER ADDRESS
    // =========================================

    if (url != null &&
        username != null &&
        password != null) {

        try {

            Class.forName(
                    "com.mysql.cj.jdbc.Driver"
            );

            String sql = """
                SELECT address_line, city, state, pincode
                FROM addresses
                WHERE user_id = ?
                ORDER BY id DESC
                LIMIT 1
                """;


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

                    if (rs.next()) {

                        addressLine =
                                rs.getString("address_line");

                        city =
                                rs.getString("city");

                        state =
                                rs.getString("state");

                        pincode =
                                rs.getString("pincode");

                        hasAddress = true;
                    }
                }
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
%>


<!DOCTYPE html>

<html lang="en">

<head>

    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>FoodieHub - My Profile</title>


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


        /* ==============================
           NAVBAR
        ============================== */

        nav {
            height: 70px;
            background: white;

            display: flex;
            align-items: center;
            justify-content: space-between;

            padding: 0 8%;

            box-shadow:
                0 2px 10px rgba(0,0,0,0.08);
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

            margin-left: 25px;

            font-weight: bold;
        }


        nav a:hover {
            color: #ff6b00;
        }


        /* ==============================
           PAGE
        ============================== */

        .page {
            width: 90%;
            max-width: 650px;

            margin: 50px auto;
        }


        .card {
            background: white;

            padding: 40px;

            border-radius: 20px;

            box-shadow:
                0 10px 30px rgba(0,0,0,0.1);
        }


        /* ==============================
           PROFILE ICON
        ============================== */

        .profile-icon {
            width: 90px;
            height: 90px;

            background: #fff1e4;

            border-radius: 50%;

            display: flex;
            align-items: center;
            justify-content: center;

            font-size: 45px;

            margin: 0 auto 20px;
        }


        h1 {
            text-align: center;

            margin-bottom: 8px;
        }


        .welcome {
            text-align: center;

            color: #777;

            margin-bottom: 30px;
        }


        /* ==============================
           INFORMATION
        ============================== */

        .info-box {
            background: #fff8f0;

            padding: 20px;

            border-radius: 15px;

            margin-bottom: 20px;
        }


        .info-title {
            font-size: 20px;

            font-weight: bold;

            color: #ff6b00;

            margin-bottom: 15px;
        }


        .row {
            display: flex;

            justify-content: space-between;

            gap: 20px;

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

            text-align: right;

            word-break: break-word;
        }


        /* ==============================
           ADDRESS
        ============================== */

        .address-box {
            background: #fff8f0;

            padding: 20px;

            border-radius: 15px;

            margin-bottom: 25px;
        }


        .address-title {
            font-size: 20px;

            font-weight: bold;

            color: #ff6b00;

            margin-bottom: 15px;
        }


        .address {
            color: #444;

            line-height: 1.7;

            margin-bottom: 15px;
        }


        .no-address {
            color: #777;

            line-height: 1.6;

            margin-bottom: 15px;
        }


        /* ==============================
           BUTTONS
        ============================== */

        .button {
            display: block;

            width: 100%;

            text-align: center;

            text-decoration: none;

            padding: 14px;

            border-radius: 10px;

            font-weight: bold;

            margin-top: 12px;
        }


        .edit-btn {
            background: #ff6b00;

            color: white;
        }


        .edit-btn:hover {
            background: #e85d00;
        }


        .order-btn {
            border: 2px solid #ff6b00;

            color: #ff6b00;
        }


        .order-btn:hover {
            background: #ff6b00;

            color: white;
        }


        /* ==============================
           MOBILE
        ============================== */

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
                margin: 30px auto;
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
        }

    </style>

</head>


<body>


<!-- ==============================
     NAVIGATION
============================== -->

<nav>

    <div class="logo">
        Foodie<span>Hub</span>
    </div>


    <div>

        <a href="index.html">
            Home
        </a>

        <a href="order.html">
            Order
        </a>

        <a href="LogoutServlet">
            Logout
        </a>

    </div>

</nav>


<!-- ==============================
     PROFILE
============================== -->

<div class="page">

    <div class="card">


        <div class="profile-icon">
            👤
        </div>


        <h1>
            My Profile
        </h1>


        <p class="welcome">
            Welcome, <%= fullName %>! 👋
        </p>


        <!-- ==========================
             PERSONAL INFORMATION
        =========================== -->

        <div class="info-box">

            <div class="info-title">
                👤 Personal Information
            </div>


            <div class="row">

                <span class="label">
                    Full Name
                </span>

                <span class="value">
                    <%= fullName %>
                </span>

            </div>


            <div class="row">

                <span class="label">
                    Email
                </span>

                <span class="value">
                    <%= email %>
                </span>

            </div>


            <div class="row">

                <span class="label">
                    Phone
                </span>

                <span class="value">
                    <%= phone %>
                </span>

            </div>

        </div>


        <!-- ==========================
             DELIVERY ADDRESS
        =========================== -->

        <div class="address-box">

            <div class="address-title">
                📍 Delivery Address
            </div>


            <% if (hasAddress) { %>

                <div class="address">

                    <strong>
                        <%= fullName %>
                    </strong>

                    <br>

                    <%= addressLine %>

                    <br>

                    <%= city %>,
                    <%= state %>

                    <br>

                    PIN:
                    <%= pincode %>

                </div>


                <a href="address.html"
                   class="button edit-btn">

                    ✏️ Edit Address

                </a>


            <% } else { %>


                <p class="no-address">

                    You haven't added a delivery address yet.

                </p>


                <a href="address.html"
                   class="button edit-btn">

                    📍 Add Address

                </a>


            <% } %>

        </div>


        <!-- ==========================
             ORDER BUTTON
        =========================== -->

        <a href="order.html"
           class="button order-btn">

            🍕 Order Food

        </a>


    </div>

</div>


</body>

</html>