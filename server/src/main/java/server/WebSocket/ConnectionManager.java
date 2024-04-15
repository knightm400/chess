package server.WebSocket;

import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.jetty.websocket.api.Session;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.google.gson.Gson;

public class ConnectionManager {
    private final ConcurrentHashMap<Session, Connection> connections = new ConcurrentHashMap<>();
    private static final Logger logger = Logger.getLogger(ConnectionManager.class.getName());
    private final Gson gson = new Gson();


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
        String jsonMessage = gson.toJson(message);
        Connection connection = connections.get(session);
        if (connection != null && session.isOpen()) {
            try {
                session.getRemote().sendString(jsonMessage);
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Error sending message to WebSocket", e);
                try {
                    session.close();
                } catch (Exception ex) {
                    logger.log(Level.SEVERE, "Error closing WebSocket session", ex);
                }
                connections.remove(session);
            }
        }
    }

    public void broadcastMessage(String message) {
        String jsonMessage = gson.toJson(message); // Serialize message to JSON
        Iterator<Session> iterator = connections.keySet().iterator();
        while (iterator.hasNext()) {
            Session session = iterator.next();
            if (session.isOpen()) {
                sendMessage(session, jsonMessage);
            } else {
                iterator.remove();
            }
        }
    }
}
