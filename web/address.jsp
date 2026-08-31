<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>FoodieHub - Delivery Address</title>

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
            margin-left: 25px;
            font-weight: bold;
        }

        nav a:hover {
            color: #ff6b00;
        }

        .page {
            width: 90%;
            max-width: 650px;
            margin: 40px auto;
        }

        .card {
            background: white;
            padding: 40px;

            border-radius: 20px;

            box-shadow:
                0 10px 30px rgba(0,0,0,0.1);
        }

        h1 {
            text-align: center;
            margin-bottom: 10px;
        }

        .subtitle {
            text-align: center;
            color: #777;
            margin-bottom: 30px;
        }

        .form-group {
            margin-bottom: 20px;
        }

        label {
            display: block;
            font-weight: bold;
            margin-bottom: 8px;
        }

        input {
            width: 100%;
            padding: 14px;

            border: 1px solid #ddd;
            border-radius: 10px;

            font-size: 16px;
        }

        input:focus {
            outline: none;
            border-color: #ff6b00;
        }

        .save-btn {
            width: 100%;

            border: none;

            padding: 15px;

            background: #ff6b00;
            color: white;

            font-size: 17px;
            font-weight: bold;

            border-radius: 10px;

            cursor: pointer;
        }

        .save-btn:hover {
            background: #e85d00;
        }

        .back {
            display: block;

            text-align: center;

            margin-top: 20px;

            color: #ff6b00;

            text-decoration: none;

            font-weight: bold;
        }

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

            .card {
                padding: 25px 20px;
            }
        }
    </style>

</head>

<body>

    <nav>

        <div class="logo">
            Foodie<span>Hub</span>
        </div>

        <div>
            <a href="index.html">Home</a>
            <a href="profile.jsp">Profile</a>
        </div>

    </nav>


    <div class="page">

        <div class="card">

            <h1>
                📍 Delivery Address
            </h1>

            <p class="subtitle">
                Add your address for food delivery
            </p>


            <form action="AddressServlet" method="post">


                <div class="form-group">

                    <label for="fullName">
                        Full Name
                    </label>

                    <input
                        type="text"
                        id="fullName"
                        name="fullName"
                        placeholder="Enter your full name"
                        required>

                </div>


                <div class="form-group">

                    <label for="phone">
                        Phone Number
                    </label>

                    <input
                        type="tel"
                        id="phone"
                        name="phone"
                        placeholder="Enter your phone number"
                        required>

                </div>


                <div class="form-group">

                    <label for="addressLine">
                        Address
                    </label>

                    <input
                        type="text"
                        id="addressLine"
                        name="addressLine"
                        placeholder="House no, street, area"
                        required>

                </div>


                <div class="form-group">

                    <label for="city">
                        City
                    </label>

                    <input
                        type="text"
                        id="city"
                        name="city"
                        placeholder="Enter your city"
                        required>

                </div>


                <div class="form-group">

                    <label for="state">
                        State
                    </label>

                    <input
                        type="text"
                        id="state"
                        name="state"
                        placeholder="Enter your state"
                        required>

                </div>


                <div class="form-group">

                    <label for="pincode">
                        PIN Code
                    </label>

                    <input
                        type="text"
                        id="pincode"
                        name="pincode"
                        placeholder="Enter PIN code"
                        maxlength="10"
                        required>

                </div>


                <button
                    type="submit"
                    class="save-btn">

                     Save Address

                </button>

            </form>


            <a href="profile.jsp" class="back">
                 Back to Profile
            </a>

        </div>

    </div>

</body>

</html>