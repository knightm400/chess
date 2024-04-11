package server.WebSocket;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.jetty.websocket.api.Session;
import java.util.Map;

public class ConnectionManager {
    private final Map<Session, Connection> connections = new ConcurrentHashMap<>();

    public void add(Session session, Connection connection) {
        connections.put(session, connection);
    }

    public void remove(Session session) {
        connections.remove(session);
    }

    public void sendMessage(Session session, String message) {
        Connection connection = connections.get(session);
        if (connection != null) {
            try {
                session.getRemote().sendString(message);
            } catch (IOException e) {
                e.printStackTrace();
                connections.remove(session);
            }
        }
    }

    public void broadcastMessage(String message) {
        for (Connection connection : connections.values()) {
            try {
                connection.getSession().getRemote().sendString(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
