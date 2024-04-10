package server;

import dataAccess.*;
import spark.Spark;
import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.sql.SQLException;

public class Server {
    private final UserDataAccess userDataAccess;
    private final GameDataAccess gameDataAccess;
    private final AuthDataAccess authDataAccess;
    private static final Map<Session, String> userSessions = new ConcurrentHashMap<>();


    public Server() {
        userDataAccess = new MySqlDataAccess();
        gameDataAccess = new MySqlDataAccess();
        authDataAccess = new MySqlDataAccess();
    }


    public int run(int desiredPort) {
        try {
            DatabaseManager.initializeDatabaseAndTables();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database and tables: " + e.getMessage());
        }

        Spark.port(desiredPort);
        Spark.staticFiles.location("web");

        Spark.webSocket("/chess", ChessWebSocketHandler.class);
        setupHttpEndpoints();

        Spark.awaitInitialization();
        return Spark.port();
    }

    private void setupHttpEndpoints() {
        ClearEndpoint.setup(userDataAccess, gameDataAccess, authDataAccess);
        RegisterEndpoint.setup(userDataAccess, authDataAccess);
        LoginEndpoint.setup(authDataAccess, userDataAccess);
        LogoutEndpoint.setup(authDataAccess);
        ListGamesEndpoint.setup(gameDataAccess, authDataAccess);
        CreateGameServiceEndpoint.setup(authDataAccess, gameDataAccess);
        JoinGameEndpoint.setup(gameDataAccess, authDataAccess);
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    @WebSocket
    public static class ChessWebSocketHandler {

        @OnWebSocketConnect
        public void onConnect(Session session) {
            userSessions.put(session, null);
            System.out.println("New connection: " + session.getRemoteAddress().getAddress());
        }

        @OnWebSocketClose
        public void onClose(Session session, int statusCode, String reason) {
            userSessions.remove(session);
            System.out.println("Closed connection: " + session.getRemoteAddress().getAddress() + " Reason: " + reason);
        }

        @OnWebSocketMessage
        public void onMessage(Session session, String message) {
            System.out.println("Message from " + session.getRemoteAddress().getAddress() + ": " + message);
            try {
                session.getRemote().sendString("Echo: " + message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}