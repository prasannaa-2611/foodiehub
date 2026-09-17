package your.package.name;

import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.OnClose;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

@ServerEndpoint("/location")
public class LocationWebSocket {

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("Delivery device connected");
    }

    @OnMessage
    public void onMessage(String message, Session session) {

        System.out.println("Location received: " + message);

    }

    @OnClose
    public void onClose(Session session) {
        System.out.println("Delivery device disconnected");
    }
}