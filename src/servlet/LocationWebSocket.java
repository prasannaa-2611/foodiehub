import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint("/location")
public class LocationWebSocket {

    // Store all connected WebSocket clients
    private static final Set<Session> connectedSessions =
            new CopyOnWriteArraySet<>();


    @OnOpen
    public void onOpen(Session session) {

        connectedSessions.add(session);

        System.out.println(
                "WebSocket connected. Total clients: "
                + connectedSessions.size()
        );
    }


    @OnMessage
    public void onMessage(String message, Session session) {

        System.out.println(
                "Location received: " + message
        );

        try {

            // -----------------------------------------
            // READ JSON VALUES
            // -----------------------------------------

            String orderIdText =
                    getValue(message, "orderId");

            String latitudeText =
                    getValue(message, "latitude");

            String longitudeText =
                    getValue(message, "longitude");


            int orderId =
                    Integer.parseInt(orderIdText);

            double latitude =
                    Double.parseDouble(latitudeText);

            double longitude =
                    Double.parseDouble(longitudeText);


            // -----------------------------------------
            // DATABASE
            // -----------------------------------------

            String url =
                    System.getenv("DB_URL");

            String username =
                    System.getenv("DB_USERNAME");

            String password =
                    System.getenv("DB_PASSWORD");


            if (url == null ||
                    username == null ||
                    password == null) {

                System.out.println(
                        "Database environment variables missing."
                );

                return;
            }


            if (url.startsWith("mysql://")) {
                url = "jdbc:" + url;
            }


            String sql = """
                    UPDATE orders
                    SET delivery_latitude = ?,
                        delivery_longitude = ?,
                        location_updated_at = CURRENT_TIMESTAMP
                    WHERE id = ?
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

                ps.setDouble(1, latitude);
                ps.setDouble(2, longitude);
                ps.setInt(3, orderId);


                int rows =
                        ps.executeUpdate();


                if (rows > 0) {

                    System.out.println(
                            "Location saved for order "
                            + orderId
                    );


                    // -----------------------------------------
                    // BROADCAST TO ALL CONNECTED CLIENTS
                    // -----------------------------------------

                    System.out.println(
                            "Broadcasting location to "
                            + connectedSessions.size()
                            + " clients."
                    );


                    for (Session connectedSession :
                            connectedSessions) {

                        if (connectedSession.isOpen()) {

                            try {

                                connectedSession
                                        .getBasicRemote()
                                        .sendText(message);

                                System.out.println(
                                        "Location sent to client: "
                                        + connectedSession.getId()
                                );

                            } catch (Exception sendError) {

                                System.out.println(
                                        "Could not send location to client."
                                );

                                sendError.printStackTrace();
                            }
                        }
                    }

                } else {

                    System.out.println(
                            "Order not found: "
                            + orderId
                    );
                }
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }


    // -----------------------------------------
    // SIMPLE JSON VALUE READER
    // -----------------------------------------

    private String getValue(
            String json,
            String key) {

        String search =
                "\"" + key + "\":";

        int start =
                json.indexOf(search);


        if (start == -1) {

            throw new IllegalArgumentException(
                    "Missing field: " + key
            );
        }


        start += search.length();


        while (
                start < json.length()
                &&
                (
                    json.charAt(start) == ' '
                    ||
                    json.charAt(start) == '"'
                )
        ) {

            start++;
        }


        int end = start;


        while (
                end < json.length()
                &&
                json.charAt(end) != ','
                &&
                json.charAt(end) != '}'
                &&
                json.charAt(end) != '"'
        ) {

            end++;
        }


        return json
                .substring(start, end)
                .trim();
    }


    @OnClose
    public void onClose(Session session) {

        connectedSessions.remove(session);

        System.out.println(
                "WebSocket disconnected. Total clients: "
                + connectedSessions.size()
        );
    }
}