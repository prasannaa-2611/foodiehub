<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>

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
    // USER DETAILS
    // =========================================

    String customerName =
            (String) session.getAttribute("fullName");

    if (customerName == null) {
        customerName = "User";
    }


    // =========================================
    // ADDRESS DETAILS
    // =========================================

    String fullName =
            (String) request.getAttribute("fullName");

    String phone =
            (String) request.getAttribute("phone");

    String addressLine =
            (String) request.getAttribute("addressLine");

    String city =
            (String) request.getAttribute("city");

    String state =
            (String) request.getAttribute("state");

    String pincode =
            (String) request.getAttribute("pincode");

    Boolean addressExists =
            (Boolean) request.getAttribute("addressExists");


    if (fullName == null) fullName = "";
    if (phone == null) phone = "";
    if (addressLine == null) addressLine = "";
    if (city == null) city = "";
    if (state == null) state = "";
    if (pincode == null) pincode = "";


    // =========================================
    // GET CART
    // =========================================

    List<Map<String, String>> cart =
            (List<Map<String, String>>) request.getAttribute("cart");

    double cartTotal = 0;


    // =========================================
    // HTML ESCAPE
    // =========================================

    String safeCustomerName = customerName
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;");

    String safeFullName = fullName
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

    <title>FoodieHub - Checkout</title>


    <style>

        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }


        body {
            font-family: Arial, sans-serif;
            background: #fff8f0;
            color: #222;
            min-height: 100vh;
        }


        /* NAVBAR */

        nav {
            background: white;
            height: 70px;
            padding: 0 8%;
            display: flex;
            align-items: center;
            justify-content: space-between;
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
            font-weight: bold;
            margin-left: 20px;
        }


        nav a:hover {
            color: #ff6b00;
        }


        /* PAGE */

        .page {
            width: 90%;
            max-width: 650px;
            margin: 50px auto;
        }


        .card {
            background: white;
            padding: 40px;
            border-radius: 20px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.1);
        }


        .title {
            text-align: center;
            margin-bottom: 10px;
            font-size: 32px;
        }


        .title span {
            color: #ff6b00;
        }


        .subtitle {
            text-align: center;
            color: #777;
            margin-bottom: 30px;
        }


        /* SECTION */

        .section-title {
            margin: 30px 0 20px;
            font-size: 22px;
        }


        /* FORM */

        .form-group {
            margin-bottom: 22px;
        }


        label {
            display: block;
            font-weight: bold;
            margin-bottom: 8px;
        }


        input,
        select {
            width: 100%;
            padding: 14px;
            border: 1px solid #ddd;
            border-radius: 10px;
            font-size: 16px;
            background: white;
        }


        input:focus,
        select:focus {
            outline: none;
            border-color: #ff6b00;
        }


        /* CART */

        .cart-box {
            background: #fff8f0;
            border-radius: 14px;
            padding: 20px;
            margin-bottom: 20px;
        }


        .cart-item {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 14px 0;
            border-bottom: 1px solid #eee;
        }


        .cart-item:last-child {
            border-bottom: none;
        }


        .item-name {
            font-weight: bold;
            font-size: 17px;
        }


        .item-details {
            color: #777;
            margin-top: 5px;
        }


        .item-total {
            font-weight: bold;
            font-size: 17px;
        }


        .cart-total {
            display: flex;
            justify-content: space-between;
            margin-top: 18px;
            padding-top: 15px;
            border-top: 2px solid #ddd;
            font-size: 20px;
        }


        .cart-total-price {
            color: #ff6b00;
            font-weight: bold;
        }


        .empty-cart {
            background: #fff3e8;
            padding: 20px;
            border-radius: 14px;
            text-align: center;
            color: #666;
        }


        .add-food {
            display: inline-block;
            margin-top: 12px;
            background: #ff6b00;
            color: white;
            text-decoration: none;
            padding: 10px 18px;
            border-radius: 8px;
            font-weight: bold;
        }


        /* SAVED ADDRESS */

        .saved-address {
            background: #fff8f0;
            border-radius: 14px;
            padding: 20px;
            line-height: 1.7;
            margin-bottom: 20px;
        }


        .saved-address strong {
            color: #222;
        }


        .edit-link {
            display: inline-block;
            margin-top: 10px;
            color: #ff6b00;
            text-decoration: none;
            font-weight: bold;
        }


        .edit-link:hover {
            text-decoration: underline;
        }


        .no-address {
            background: #fff3e8;
            padding: 20px;
            border-radius: 14px;
            text-align: center;
            color: #666;
        }


        .add-address {
            display: inline-block;
            margin-top: 12px;
            background: #ff6b00;
            color: white;
            text-decoration: none;
            padding: 10px 18px;
            border-radius: 8px;
            font-weight: bold;
        }


        /* BUTTON */

        .submit-btn {
            width: 100%;
            border: none;
            padding: 15px;
            background: #ff6b00;
            color: white;
            font-size: 17px;
            font-weight: bold;
            border-radius: 10px;
            cursor: pointer;
            margin-top: 10px;
        }


        .submit-btn:hover {
            background: #e85d00;
        }


        .submit-btn:disabled {
            background: #aaa;
            cursor: not-allowed;
        }


        .back {
            display: block;
            text-align: center;
            margin-top: 25px;
            color: #ff6b00;
            text-decoration: none;
            font-weight: bold;
        }


        /* MOBILE */

        @media (max-width: 600px) {

            nav {
                padding: 0 20px;
            }


            nav a {
                margin-left: 10px;
            }


            .page {
                margin: 30px auto;
            }


            .card {
                padding: 25px 20px;
            }


            .title {
                font-size: 27px;
            }


            .cart-item {
                gap: 15px;
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

            <a href="index.html">
                Home
            </a>

            <a href="profile.jsp">
                Profile
            </a>

        </div>

    </nav>



    <!-- ORDER PAGE -->

    <div class="page">

        <div class="card">


            <h1 class="title">
                Checkout <span>Order</span>
            </h1>


            <p class="subtitle">
                Review your cart and place your order 🍴
            </p>



            <!-- ORDER FORM -->

            <form action="OrderServlet" method="post">


                <!-- CUSTOMER -->

                <h2 class="section-title">
                    Customer Information
                </h2>


                <div class="form-group">

                    <label for="customerName">
                        Your Name
                    </label>


                    <input
                        type="text"
                        id="customerName"
                        name="customerName"
                        value="<%= safeCustomerName %>"
                        readonly>

                </div>



                <!-- CART -->

                <h2 class="section-title">
                    🛒 Order Details
                </h2>


                <% if (cart == null || cart.isEmpty()) { %>


                    <div class="empty-cart">

                        <p>
                            Your cart is empty.
                        </p>


                        <a href="MenuServlet" class="add-food">
                            + Add Food
                        </a>

                    </div>


                <% } else { %>


                    <div class="cart-box">


                        <% for (Map<String, String> item : cart) { %>


                            <%
                                String itemName = item.get("name");
                                String itemPrice = item.get("price");
                                String itemQuantity = item.get("quantity");

                                double price =
                                        Double.parseDouble(itemPrice);

                                int quantity =
                                        Integer.parseInt(itemQuantity);

                                double itemTotal =
                                        price * quantity;

                                cartTotal += itemTotal;
                            %>


                            <div class="cart-item">


                                <div>

                                    <div class="item-name">
                                        <%= itemName %>
                                    </div>


                                    <div class="item-details">
                                        ₹<%= String.format("%.0f", price) %>
                                        ×
                                        <%= quantity %>
                                    </div>

                                </div>


                                <div class="item-total">
                                    ₹<%= String.format("%.0f", itemTotal) %>
                                </div>


                            </div>


                        <% } %>



                        <!-- TOTAL -->

                        <div class="cart-total">

                            <strong>
                                Total
                            </strong>


                            <span class="cart-total-price">
                                ₹<%= String.format("%.0f", cartTotal) %>
                            </span>

                        </div>


                    </div>


                <% } %>



                <!-- DELIVERY ADDRESS -->

                <h2 class="section-title">
                    📍 Delivery Address
                </h2>



                <% if (Boolean.TRUE.equals(addressExists)) { %>


                    <div class="saved-address">


                        <strong>
                            <%= safeFullName %>
                        </strong>


                        <br>


                        <%= safePhone %>


                        <br>


                        <%= safeAddress %>


                        <br>


                        <%= safeCity %>,
                        <%= safeState %>
                        -
                        <%= safePincode %>


                        <br>


                        <a href="AddressServlet"
                           class="edit-link">
                            Edit Address
                        </a>


                    </div>



                    <!-- Hidden address values -->

                    <input
                        type="hidden"
                        name="fullName"
                        value="<%= safeFullName %>">


                    <input
                        type="hidden"
                        name="phone"
                        value="<%= safePhone %>">


                    <input
                        type="hidden"
                        name="addressLine"
                        value="<%= safeAddress %>">


                    <input
                        type="hidden"
                        name="city"
                        value="<%= safeCity %>">


                    <input
                        type="hidden"
                        name="state"
                        value="<%= safeState %>">


                    <input
                        type="hidden"
                        name="pincode"
                        value="<%= safePincode %>">


                <% } else { %>


                    <div class="no-address">

                        <p>
                            You don't have a delivery address saved.
                        </p>


                        <a href="AddressServlet"
                           class="add-address">
                            + Add Delivery Address
                        </a>

                    </div>


                <% } %>



                <!-- SUBMIT -->

                <button
                    type="submit"
                    class="submit-btn"
                    <% if (!Boolean.TRUE.equals(addressExists)
                           || cart == null
                           || cart.isEmpty()) { %>
                        disabled
                    <% } %>>

                    Place Order

                </button>


            </form>



            <a href="index.html"
               class="back">
                Back to Home
            </a>


        </div>

    </div>



    <footer>

        © 2026 FoodieHub • Delicious food, happy moments ❤️

    </footer>


</body>

</html>