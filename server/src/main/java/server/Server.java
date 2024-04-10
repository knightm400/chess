package server;

import dataAccess.*;
import spark.Spark;
import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.sql.SQLException;
import com.google.gson.Gson;
import webSocketMessages.userCommands.UserGameCommand;

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
        private static final Gson gson = new Gson();

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
                UserGameCommand command = gson.fromJson(message, UserGameCommand.class);
                System.out.println("Received command: " + command.getCommandType());
                switch (command.getCommandType()) {
                    case JOIN_PLAYER:
                        break;
                    case JOIN_OBSERVER:
                        break;
                    case MAKE_MOVE:
                        break;
                    case LEAVE:
                        break;
                    case RESIGN:
                        break;
                    default:
                        System.out.println("Unknown command received.");
                        break;
                }

                session.getRemote().sendString("Command received: " + command.getCommandType());
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    session.getRemote().sendString("Error processing your command");
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }
    }
}