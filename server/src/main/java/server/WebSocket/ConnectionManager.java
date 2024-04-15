package server.WebSocket;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import javax.websocket.Session;
import java.util.Map;

public class ConnectionManager {
    private final ConcurrentHashMap<Session, Connection> connections = new ConcurrentHashMap<>();

    public void add(Session session, Connection connection) {
        connections.put(session, connection);
    }

    public void remove(Session session) {
        connections.remove(session);
    }

    public Connection getConnection(Session session) {
        return connections.get(session);
    }

    public void sendMessage(Session session, String message) {
        Connection connection = connections.get(session);
        if (connection != null && session.isOpen()) {
            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                e.printStackTrace();
                connections.remove(session);
            }
        }
    }

    public void broadcastMessage(String message) {
        for (Session session : connections.keySet()) {
            sendMessage(session, message);
        }
    }
}
