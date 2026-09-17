<%@ page contentType="text/html;charset=UTF-8" %>

<!DOCTYPE html>
<html>
<head>

    <meta charset="UTF-8">

    <title>Track Order - FoodieHub</title>

    <!-- Leaflet CSS -->
    <link
        rel="stylesheet"
        href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css"
    >

    <style>

        body {
            font-family: Arial, sans-serif;
            background: #fff8f1;
            text-align: center;
            padding: 40px;
        }

        .container {
            max-width: 700px;
            margin: auto;
            background: white;
            padding: 30px;
            border-radius: 15px;
            box-shadow: 0 4px 15px rgba(0,0,0,0.1);
        }

        h1 {
            color: #ff6b00;
        }

        .order-info {
            text-align: left;
            margin: 25px 0;
            padding: 20px;
            background: #fff3e8;
            border-radius: 10px;
        }

        #map {
            width: 100%;
            height: 400px;
            margin-top: 25px;
            border-radius: 12px;
            overflow: hidden;
        }

        #locationStatus {
            margin-top: 10px;
            font-size: 14px;
        }

        .steps {
            display: flex;
            justify-content: space-between;
            margin-top: 40px;
        }

        .step {
            width: 23%;
        }

        .circle {
            width: 45px;
            height: 45px;
            line-height: 45px;
            margin: auto;
            border-radius: 50%;
            background: #ddd;
            font-weight: bold;
        }

        .active {
            background: #ff6b00;
            color: white;
        }

        .step p {
            font-size: 13px;
            margin-top: 10px;
        }

        .home-btn {
            display: inline-block;
            margin-top: 30px;
            padding: 12px 20px;
            background: #ff6b00;
            color: white;
            text-decoration: none;
            border-radius: 8px;
        }

        @media (max-width: 600px) {

            body {
                padding: 15px;
            }

            .container {
                padding: 20px;
            }

            #map {
                height: 350px;
            }

            .steps {
                flex-wrap: wrap;
                gap: 20px;
            }

            .step {
                width: 45%;
            }
        }

    </style>

</head>


<body>

<div class="container">

    <h1>📦 Track Your Order</h1>


    <!-- =====================================
         ORDER INFORMATION
         ===================================== -->

    <div class="order-info">

        <p>
            <strong>Order ID:</strong>
            ${orderId}
        </p>

        <p>
            <strong>Customer:</strong>
            ${customerName}
        </p>

        <p>
            <strong>Food:</strong>
            ${foodName}
        </p>

        <p>
            <strong>Quantity:</strong>
            ${quantity}
        </p>

        <p>
            <strong>Current Status:</strong>
            ${status}
        </p>

    </div>


    <!-- =====================================
         LIVE MAP
         ===================================== -->

    <div id="map"></div>

    <p id="locationStatus">
        ${locationMessage}
    </p>


    <!-- =====================================
         ORDER PROGRESS
         ===================================== -->

    <div class="steps">


        <!-- ORDER PLACED -->

        <div class="step">

            <div class="circle ${orderPlacedClass}">
                ✓
            </div>

            <p>
                Order Placed
            </p>

        </div>


        <!-- PREPARING -->

        <div class="step">

            <div class="circle ${preparingClass}">
                ✓
            </div>

            <p>
                Preparing
            </p>

        </div>


        <!-- OUT FOR DELIVERY -->

        <div class="step">

            <div class="circle ${outForDeliveryClass}">
                ✓
            </div>

            <p>
                Out for Delivery
            </p>

        </div>


        <!-- DELIVERED -->

        <div class="step">

            <div class="circle ${deliveredClass}">
                ✓
            </div>

            <p>
                Delivered
            </p>

        </div>

    </div>


    <!-- =====================================
         BACK HOME
         ===================================== -->

    <a
        href="index.html"
        class="home-btn"
    >
        ← Back to Home
    </a>

</div>


<!-- =====================================
     LEAFLET JAVASCRIPT
     ===================================== -->

<script
    src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js">
</script>


<script>

    // =====================================
    // ORDER ID
    // =====================================

    const orderId = Number("${orderId}");


    // =====================================
    // EXISTING GPS LOCATION
    // =====================================

    const existingLatitude =
        ${deliveryLatitude == null ? "null" : deliveryLatitude};

    const existingLongitude =
        ${deliveryLongitude == null ? "null" : deliveryLongitude};


    // =====================================
    // CREATE MAP
    // =====================================

    const map =
        L.map("map").setView(
            [16.5, 80.6],
            7
        );


    // =====================================
    // OPEN STREET MAP
    // =====================================

    L.tileLayer(
        "https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png",
        {
            attribution:
                "&copy; OpenStreetMap contributors"
        }
    ).addTo(map);


    // Delivery marker

    let deliveryMarker = null;


    // Location status text

    const locationStatus =
        document.getElementById(
            "locationStatus"
        );


    // =====================================
    // SHOW EXISTING LOCATION
    // =====================================

    if (
        existingLatitude !== null &&
        existingLongitude !== null
    ) {

        const position = [
            existingLatitude,
            existingLongitude
        ];


        deliveryMarker =
            L.marker(position)
                .addTo(map)
                .bindPopup(
                    "🚚 Delivery Location"
                );


        map.setView(
            position,
            15
        );

    }


    // =====================================
    // WEBSOCKET CONNECTION
    // =====================================

    const protocol =
        location.protocol === "https:"
            ? "wss://"
            : "ws://";


    const socket =
        new WebSocket(
            protocol +
            location.host +
            "/location"
        );


    // =====================================
    // WEBSOCKET CONNECTED
    // =====================================

    socket.onopen = function () {

        console.log(
            "Tracking WebSocket connected"
        );

    };


    // =====================================
    // RECEIVE LIVE LOCATION
    // =====================================

    socket.onmessage = function(event) {

        try {

            const data =
                JSON.parse(event.data);


            // =================================
            // IGNORE OTHER ORDERS
            // =================================

            if (
                String(data.orderId) !==
                String(orderId)
            ) {

                return;

            }


            // =================================
            // GET GPS COORDINATES
            // =================================

            const latitude =
                parseFloat(
                    data.latitude
                );

            const longitude =
                parseFloat(
                    data.longitude
                );


            // Invalid coordinates

            if (
                isNaN(latitude) ||
                isNaN(longitude)
            ) {

                return;

            }


            const position = [
                latitude,
                longitude
            ];


            // =================================
            // CREATE MARKER
            // =================================

            if (
                deliveryMarker === null
            ) {

                deliveryMarker =
                    L.marker(position)
                        .addTo(map)
                        .bindPopup(
                            "🚚 Delivery Location"
                        );


                map.setView(
                    position,
                    15
                );

            }


            // =================================
            // MOVE MARKER
            // =================================

            else {

                deliveryMarker.setLatLng(
                    position
                );

            }


            // =================================
            // UPDATE MESSAGE
            // =================================

            locationStatus.innerText =
                "Delivery location updated 📍";

        }


        catch(error) {

            console.log(
                "Location message error:",
                error
            );

        }

    };


    // =====================================
    // WEBSOCKET CLOSED
    // =====================================

    socket.onclose = function() {

        locationStatus.innerText =
            "Live tracking disconnected ❌";

    };


    // =====================================
    // WEBSOCKET ERROR
    // =====================================

    socket.onerror = function() {

        locationStatus.innerText =
            "Live tracking connection error ❌";

    };

</script>


</body>
</html>