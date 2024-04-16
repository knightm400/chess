package server.WebSocket;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.jetty.websocket.api.Session;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.google.gson.Gson;

public class ConnectionManager {
    private final ConcurrentHashMap<Session, Connection> connections = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, Set<Session>> gameSessions = new ConcurrentHashMap<>();

    private static final Logger logger = Logger.getLogger(ConnectionManager.class.getName());
    private final Gson gson = new Gson();


    public void add(Session session, Connection connection) {
        connections.put(session, connection);
        gameSessions.computeIfAbsent(connection.getGameId(), k -> ConcurrentHashMap.newKeySet()).add(session);
    }

    public void remove(Session session) {
        Connection connection = connections.remove(session);
        if (connection != null && connection.getGameId() != null) {
            Set<Session> sessions = gameSessions.get(connection.getGameId());
            if (sessions != null) {
                sessions.remove(session);
                if (sessions.isEmpty()) {
                    gameSessions.remove(connection.getGameId());
                }
            }
        }
    }

    public Connection getConnection(Session session) {
        return connections.get(session);
    }

    public List<Session> getSessionsForGame(int gameId) {
        return new ArrayList<>(gameSessions.getOrDefault(gameId, Collections.emptySet()));
    }

    public void sendMessage(Session session, String message) {
        if (session.isOpen()) {
            try {
                session.getRemote().sendString(message);
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Error sending message to WebSocket", e);
                try {
                    session.close();
                } catch (Exception ex) {
                    logger.log(Level.SEVERE, "Error closing WebSocket session", ex);
                }
                remove(session);
            }
        }
    }

    public void broadcastMessage(int gameId, String message, Session excludeSession) {
        List<Session> sessions = getSessionsForGame(gameId);
        for (Session session : sessions) {
            if (!session.equals(excludeSession)) {
                sendMessage(session, message);
            }
        }
    }
}
