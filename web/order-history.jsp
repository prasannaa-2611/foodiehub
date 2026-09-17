<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>

<%
    List<Map<String, Object>> orders =
            (List<Map<String, Object>>) request.getAttribute("orders");
%>

<!DOCTYPE html>
<html>
<head>
    <title>My Order History</title>

    <style>
        body {
            font-family: Arial, sans-serif;
            background: #f5f5f5;
            margin: 0;
            padding: 30px;
        }

        .container {
            max-width: 800px;
            margin: auto;
        }

        h1 {
            text-align: center;
        }

        .order {
            background: white;
            padding: 20px;
            margin: 15px 0;
            border-radius: 10px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
        }

        .order p {
            margin: 8px 0;
        }

        .status {
            font-weight: bold;
        }

        .buttons {
            text-align: center;
            margin-top: 25px;
        }

        button {
            padding: 10px 20px;
            border: none;
            border-radius: 6px;
            cursor: pointer;
            margin: 5px;
        }
    </style>
</head>

<body>

<div class="container">

    <h1>📦 My Order History</h1>

    <%
        if (orders == null || orders.isEmpty()) {
    %>

        <div class="order">
            <p>No orders found.</p>
        </div>

    <%
        } else {

            for (Map<String, Object> order : orders) {
    %>

        <div class="order">

            <p>
                <strong>Order ID:</strong>
                <%= order.get("id") %>
            </p>

            <p>
                <strong>Food:</strong>
                <%= order.get("foodName") %>
            </p>

            <p>
                <strong>Quantity:</strong>
                <%= order.get("quantity") %>
            </p>

            <p class="status">
                <strong>Status:</strong>
                <%= order.get("status") %>
            </p>

            <p>
                <a href="TrackOrderServlet?orderId=<%= order.get("id") %>">
                    🚚 Track This Order
                </a>
            </p>

        </div>

    <%
            }
        }
    %>

    <div class="buttons">
        <button onclick="location.href='profile.jsp'">
            👤 Back to Profile
        </button>

        <button onclick="location.href='index.html'">
            🏠 Home
        </button>
    </div>

</div>

</body>
</html>