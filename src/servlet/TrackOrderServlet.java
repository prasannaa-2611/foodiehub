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

@*WebServlet*("/TrackOrderServlet")

public class TrackOrderServlet extends HttpServlet {

    @*Override*

    protected *void* doGet(

            *HttpServletRequest* request,

            *HttpServletResponse* response)

            throws *ServletException*, *IOException* {

        *// =========================================*

        *// CHECK LOGIN*

        *// =========================================*

        *HttpSession* session = request.getSession(false);

        *if* (session == null ||

                session.getAttribute("userId") == null) {

            response.sendRedirect("login.html");

            *return*;

        }

        *Integer* userId =

                (Integer) session.getAttribute("userId");



        *// =========================================*

        *// GET ORDER ID*

        *// =========================================*

        *String* orderIdText =

                request.getParameter("orderId");

        *if* (orderIdText == null ||

                orderIdText.trim().isEmpty()) {

            response.sendError(

                    HttpServletResponse.SC\_BAD\_REQUEST,

                    "Order ID is missing."

            );

            *return*;

        }



        *int* orderId;

        *try* {

            orderId =

                    Integer.parseInt(orderIdText);

        } *catch* (*NumberFormatException* e) {

            response.sendError(

                    HttpServletResponse.SC\_BAD\_REQUEST,

                    "Invalid Order ID."

            );

            *return*;

        }



        *// =========================================*

        *// DATABASE DETAILS*

        *// =========================================*

        *String* url =

                System.getenv("DB\_URL");

        *String* username =

                System.getenv("DB\_USERNAME");

        *String* password =

                System.getenv("DB\_PASSWORD");



        *if* (url == null ||

                username == null ||

                password == null) {

            response.sendError(

                    HttpServletResponse.SC\_INTERNAL\_SERVER\_ERROR,

                    "Database configuration is missing."

            );

            *return*;

        }



        *if* (url.startsWith("mysql://")) {

            url = "jdbc:" + url;

        }



        *// =========================================*

        *// SQL*

        *// =========================================*

        *String* sql = """

                SELECT

                    id,

                    customer\_name,

                    food\_name,

                    quantity,

                    status,

                    delivery\_latitude,

                    delivery\_longitude

                FROM orders

                WHERE id = ?

                AND user\_id = ?

                """;



        *// =========================================*

        *// DATABASE*

        *// =========================================*

        *try* {

            Class.forName(

                    "com.mysql.cj.jdbc.Driver"

            );



            *try* (

                *Connection* con =

                        DriverManager.getConnection(

                                url,

                                username,

                                password

                        );

                *PreparedStatement* ps =

                        con.prepareStatement(sql)

            ) {

                ps.setInt(1, orderId);

                ps.setInt(2, userId);



                *try* (*ResultSet* rs =

                             ps.executeQuery()) {



                    *// =========================================*

                    *// ORDER NOT FOUND*

                    *// =========================================*

                    *if* (!rs.next()) {

                        response.sendError(

                                HttpServletResponse.SC\_NOT\_FOUND,

                                "Order not found."

                        );

                        *return*;

                    }



                    *// =========================================*

                    *// GET ORDER DATA*

                    *// =========================================*

                    *String* customerName =

                            rs.getString(

                                    "customer\_name"

                            );

                    *String* foodName =

                            rs.getString(

                                    "food\_name"

                            );

                    *int* quantity =

                            rs.getInt(

                                    "quantity"

                            );

                    *String* status =

                            rs.getString(

                                    "status"

                            );



                    *Double* deliveryLatitude =

                            (Double) rs.getObject(

                                    "delivery\_latitude"

                            );

                    *Double* deliveryLongitude =

                            (Double) rs.getObject(

                                    "delivery\_longitude"

                            );



                    *// =========================================*

                    *// SEND DATA TO JSP*

                    *// =========================================*

                    request.setAttribute(

                            "orderId",

                            orderId

                    );

                    request.setAttribute(

                            "customerName",

                            escapeHtml(customerName)

                    );

                    request.setAttribute(

                            "foodName",

                            escapeHtml(foodName)

                    );

                    request.setAttribute(

                            "quantity",

                            quantity

                    );

                    request.setAttribute(

                            "status",

                            escapeHtml(status)

                    );



                    *// =========================================*

                    *// LOCATION DATA*

                    *// =========================================*

                    *if* (deliveryLatitude != null &&

                            deliveryLongitude != null) {

                        request.setAttribute(

                                "deliveryLatitude",

                                deliveryLatitude

                        );

                        request.setAttribute(

                                "deliveryLongitude",

                                deliveryLongitude

                        );

                        request.setAttribute(

                                "locationMessage",

                                "Delivery location available 📍"

                        );

                    } *else* {

                        request.setAttribute(

                                "deliveryLatitude",

                                "null"

                        );

                        request.setAttribute(

                                "deliveryLongitude",

                                "null"

                        );

                        request.setAttribute(

                                "locationMessage",

                                "Waiting for delivery location... 📍"

                        );

                    }



                    *// =========================================*

                    *// ORDER STATUS CLASSES*

                    *// =========================================*

                    request.setAttribute(

                            "orderPlacedClass",

                            getActiveClass(

                                    status,

                                    "Order Placed"

                            )

                    );

                    request.setAttribute(

                            "preparingClass",

                            getActiveClass(

                                    status,

                                    "Preparing"

                            )

                    );

                    request.setAttribute(

                            "outForDeliveryClass",

                            getActiveClass(

                                    status,

                                    "Out for Delivery"

                            )

                    );

                    request.setAttribute(

                            "deliveredClass",

                            getActiveClass(

                                    status,

                                    "Delivered"

                            )

                    );



                    *// =========================================*

                    *// OPEN JSP PAGE*

                    *// =========================================*

                    request.getRequestDispatcher(

                            "/track-order.jsp"

                    ).forward(

                            request,

                            response

                    );

                }

            }

        } *catch* (*Exception* e) {

            e.printStackTrace();

            response.sendError(

                    HttpServletResponse.SC\_INTERNAL\_SERVER\_ERROR,

                    "Something went wrong."

            );

        }

    }



    *// =========================================*

    *// DETERMINE ACTIVE STEP*

    *// =========================================*

    private *String* getActiveClass(

            *String* currentStatus,

            *String* stepStatus) {

        *int* currentIndex =

                getStatusIndex(currentStatus);

        *int* stepIndex =

                getStatusIndex(stepStatus);



        *if* (stepIndex <= currentIndex) {

            *return* "active";

        }

        *return* "";

    }



    private *int* getStatusIndex(

            *String* status) {

        *if* ("Preparing".equals(status)) {

            *return* 1;

        }

        *if* ("Out for Delivery".equals(status)) {

            *return* 2;

        }

        *if* ("Delivered".equals(status)) {

            *return* 3;

        }

        *return* 0;

    }



    *// =========================================*

    *// HTML ESCAPE*

    *// =========================================*

    private *String* escapeHtml(*String* text) {

        *if* (text == null) {

            *return* "";

        }

        *return* text

                .replace("&", "&amp;")

                .replace("<", "&lt;")

                .replace(">", "&gt;")

                .replace("\\"", "&quot;")

                .replace("'", "&#39;");

    }

}