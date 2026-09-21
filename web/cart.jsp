<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>

<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>FoodieHub - Cart</title>

    <style>

        :root {
            --cream: #fffaf3;
            --peach: #f6d8c0;
            --peach-light: #fff1e5;
            --terracotta: #d96b3a;
            --terracotta-dark: #bd5630;
            --brown: #3b2922;
            --brown-light: #684b3e;
            --muted: #806f66;
            --white: #ffffff;
        }

        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: Arial, Helvetica, sans-serif;

            background:
                linear-gradient(
                    135deg,
                    var(--cream),
                    var(--peach-light)
                );

            color: var(--brown);

            min-height: 100vh;
        }


        /* =========================
           NAVBAR
        ========================= */

        nav {
            height: 75px;

            padding: 0 7%;

            display: flex;

            align-items: center;

            justify-content: space-between;

            background:
                rgba(255, 250, 243, 0.95);

            border-bottom:
                1px solid rgba(59, 41, 34, 0.08);

            box-shadow:
                0 4px 20px
                rgba(59, 41, 34, 0.06);
        }

        .logo {
            text-decoration: none;

            font-size: 28px;

            font-weight: 800;

            color: var(--terracotta);
        }

        .logo span {
            color: var(--brown);
        }

        .back-link {
            text-decoration: none;

            color: var(--brown-light);

            font-weight: 600;

            transition: 0.3s;
        }

        .back-link:hover {
            color: var(--terracotta);
        }


        /* =========================
           MAIN
        ========================= */

        .container {
            max-width: 1000px;

            margin: 50px auto;

            padding: 0 20px;
        }

        .heading {
            text-align: center;

            margin-bottom: 35px;

            animation:
                fadeDown 0.7s ease;
        }

        .heading h1 {
            font-size: 42px;

            color: var(--brown);

            margin-bottom: 10px;
        }

        .heading p {
            color: var(--muted);
        }


        /* =========================
           CART
        ========================= */

        .cart-box {
            background: var(--white);

            border-radius: 20px;

            padding: 25px;

            box-shadow:
                0 15px 40px
                rgba(59, 41, 34, 0.08);

            animation:
                fadeUp 0.8s ease;
        }

        .cart-item {
            display: flex;

            align-items: center;

            justify-content: space-between;

            gap: 20px;

            padding: 20px 5px;

            border-bottom:
                1px solid #eee1d7;

            transition: 0.3s;
        }

        .cart-item:hover {
            transform: translateX(5px);
        }

        .food-info {
            flex: 1;
        }

        .food-name {
            font-size: 20px;

            font-weight: bold;

            color: var(--brown);

            margin-bottom: 6px;
        }

        .food-price {
            color: var(--muted);

            font-size: 14px;
        }

        .quantity {
            background: var(--peach-light);

            color: var(--terracotta);

            padding: 8px 15px;

            border-radius: 20px;

            font-weight: bold;

            white-space: nowrap;
        }

        .item-total {
            font-size: 18px;

            font-weight: bold;

            color: var(--brown);

            min-width: 90px;

            text-align: right;
        }


        /* =========================
           REMOVE BUTTON
        ========================= */

        .remove-btn {
            text-decoration: none;

            color: #a44b3b;

            background: #fbe9e5;

            padding: 8px 13px;

            border-radius: 8px;

            font-size: 13px;

            font-weight: bold;

            transition: 0.3s;
        }

        .remove-btn:hover {
            background: #f2d1ca;

            transform: scale(1.05);
        }


        /* =========================
           SUMMARY
        ========================= */

        .summary {
            margin-top: 25px;

            padding-top: 20px;

            border-top:
                2px solid #eee1d7;

            display: flex;

            justify-content: space-between;

            align-items: center;
        }

        .summary-text {
            color: var(--muted);

            font-size: 16px;
        }

        .grand-total {
            font-size: 28px;

            font-weight: bold;

            color: var(--terracotta);
        }


        /* =========================
           BUTTONS
        ========================= */

        .actions {
            display: flex;

            justify-content: space-between;

            align-items: center;

            gap: 15px;

            margin-top: 30px;
        }

        .continue-btn,
        .clear-btn,
        .checkout-btn {
            display: inline-block;

            text-decoration: none;

            padding: 13px 22px;

            border-radius: 25px;

            font-weight: bold;

            transition: 0.3s;
        }

        .continue-btn {
            background: var(--peach-light);

            color: var(--terracotta);
        }

        .continue-btn:hover {
            background: var(--peach);

            transform: translateY(-3px);
        }

        .clear-btn {
            background: #fbe9e5;

            color: #a44b3b;

            border: none;

            cursor: pointer;

            font-size: 14px;
        }

        .clear-btn:hover {
            background: #f2d1ca;

            transform: translateY(-3px);
        }

        .checkout-btn {
            background: var(--terracotta);

            color: white;

            box-shadow:
                0 8px 20px
                rgba(217, 107, 58, 0.22);
        }

        .checkout-btn:hover {
            background: var(--terracotta-dark);

            transform: translateY(-3px);

            box-shadow:
                0 12px 25px
                rgba(217, 107, 58, 0.28);
        }


        /* =========================
           EMPTY CART
        ========================= */

        .empty-cart {
            text-align: center;

            padding: 60px 20px;
        }

        .empty-icon {
            font-size: 75px;

            margin-bottom: 20px;

            animation:
                floating 3s ease-in-out infinite;
        }

        .empty-cart h2 {
            font-size: 28px;

            margin-bottom: 10px;
        }

        .empty-cart p {
            color: var(--muted);

            margin-bottom: 25px;
        }


        /* =========================
           ANIMATIONS
        ========================= */

        @keyframes fadeDown {

            from {
                opacity: 0;

                transform:
                    translateY(-25px);
            }

            to {
                opacity: 1;

                transform:
                    translateY(0);
            }
        }

        @keyframes fadeUp {

            from {
                opacity: 0;

                transform:
                    translateY(30px);
            }

            to {
                opacity: 1;

                transform:
                    translateY(0);
            }
        }

        @keyframes floating {

            0%,
            100% {
                transform:
                    translateY(0);
            }

            50% {
                transform:
                    translateY(-12px);
            }
        }


        /* =========================
           MOBILE
        ========================= */

        @media (max-width: 650px) {

            nav {
                padding: 0 5%;
            }

            .container {
                margin-top: 35px;
            }

            .heading h1 {
                font-size: 34px;
            }

            .cart-item {
                flex-wrap: wrap;
            }

            .food-info {
                width: 100%;
                flex-basis: 100%;
            }

            .item-total {
                text-align: left;
            }

            .summary {
                flex-direction: column;

                align-items: flex-start;

                gap: 8px;
            }

            .actions {
                flex-direction: column;

                align-items: stretch;
            }

            .continue-btn,
            .clear-btn,
            .checkout-btn {
                text-align: center;

                width: 100%;
            }
        }

    </style>

</head>


<body>


    <!-- =========================
         NAVBAR
    ========================= -->

    <nav>

        <a href="index.html"
           class="logo">

            Foodie<span>Hub</span>

        </a>

        <a href="MenuServlet"
           class="back-link">

             Back to Menu

        </a>

    </nav>



    <!-- =========================
         MAIN
    ========================= -->

    <div class="container">


        <div class="heading">

            <h1>
                 Your Cart
            </h1>

            <p>
                Review your items before placing your order.
            </p>

        </div>


        <div class="cart-box">

<%

    List<Map<String, String>> cart =
            (List<Map<String, String>>)
            session.getAttribute("cart");

    if (cart == null || cart.isEmpty()) {

%>


            <!-- EMPTY CART -->

            <div class="empty-cart">

                <div class="empty-icon">
                    
                </div>

                <h2>
                    Your cart is empty
                </h2>

                <p>
                    Looks like you haven't added
                    anything yet.
                </p>

                <a href="MenuServlet"
                   class="checkout-btn">

                    Explore Menu

                </a>

            </div>


<%

    } else {

        double grandTotal = 0;

        for (Map<String, String> item : cart) {

            String name =
                    item.get("name");

            double price =
                    Double.parseDouble(
                            item.get("price")
                    );

            int quantity =
                    Integer.parseInt(
                            item.get("quantity")
                    );

            double itemTotal =
                    price * quantity;

            grandTotal += itemTotal;

%>


            <!-- CART ITEM -->

            <div class="cart-item">


                <div class="food-info">

                    <div class="food-name">

                        <%= name %>

                    </div>

                    <div class="food-price">

                        <%= String.format(
                            "%.2f",
                            price
                        ) %>
                        each

                    </div>

                </div>


                <div class="quantity">

                    Qty:
                    <%= quantity %>

                </div>


                <div class="item-total">

                    <%= String.format(
                        "%.2f",
                        itemTotal
                    ) %>

                </div>


                <a href="RemoveFromCartServlet?foodId=<%= item.get("id") %>"
                   class="remove-btn">

                    Remove

                </a>


            </div>


<%

        }

%>


            <!-- SUMMARY -->

            <div class="summary">

                <div class="summary-text">

                    Total Amount

                </div>

                <div class="grand-total">

                    <%= String.format(
                        "%.2f",
                        grandTotal
                    ) %>

                </div>

            </div>


            <!-- ACTIONS -->

            <div class="actions">


                <a href="MenuServlet"
                   class="continue-btn">

                     Continue Shopping

                </a>


                <a href="ClearCartServlet"
                   class="clear-btn">

                     Clear Cart

                </a>


                <a href="OrderPageServlet"
                   class="checkout-btn">

                     Checkout

                </a>


            </div>


<%

    }

%>

        </div>

    </div>


</body>

</html>