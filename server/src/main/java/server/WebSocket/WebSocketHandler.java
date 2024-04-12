package server.WebSocket;

import dataAccess.AuthDataAccess;
import dataAccess.DataAccessException;
import model.AuthData;
import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;
import com.google.gson.Gson;
import server.WebSocket.Connection;
import server.WebSocket.ConnectionManager;
import webSocketMessages.userCommands.UserGameCommand;
import webSocketMessages.serverMessages.ErrorMessage;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {
    private static final Gson gson = new Gson();
    private final ConnectionManager connectionManager;
    private final AuthDataAccess authDataAccess;

    public WebSocketHandler(AuthDataAccess authDataAccess, ConnectionManager connectionManager) {
        this.authDataAccess = authDataAccess;
        this.connectionManager = connectionManager;
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.println("New connection: " + session.getRemoteAddress().getAddress());
        try {
            String token = session.getUpgradeRequest().getParameterMap().get("authToken").get(0);
            AuthData authData = authDataAccess.getAuth(token);
            if (authData != null) {
                Connection connection = new Connection(session, authData);
                connectionManager.add(session, connection);
            } else {
                session.close(new CloseStatus(1008, "Authentication failed"));
            }
        } catch (DataAccessException e) {
            System.out.println("Failed to authenticate: " + e.getMessage());
        }
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        System.out.println("Closed connection: " + session.getRemoteAddress().getAddress() + ", Reason: " + reason);
        connectionManager.remove(session);
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        try {
            UserGameCommand command = gson.fromJson(message, UserGameCommand.class);
            Connection connection = connectionManager.getConnection(session);
            if (connection == null || connection.getAuthData() == null) {
                session.getRemote().sendString(gson.toJson(new ErrorMessage("Authentication required")));
                return;
            }

            AuthData authData = connection.getAuthData();
            switch (command.getCommandType()) {
                case JOIN_PLAYER:
                    // Implement logic to handle a player joining a game
                    break;
                case JOIN_OBSERVER:
                    // Implement logic to handle an observer joining a game
                    break;
                case MAKE_MOVE:
                    // Implement logic to handle a player making a move in a game
                    break;
                case LEAVE:
                    // Implement logic to handle a player or observer leaving a game
                    break;
                case RESIGN:
                    // Implement logic to handle a player resigning from a game
                    break;
                default:
                    session.getRemote().sendString(gson.toJson(new ErrorMessage("Unknown command")));
                    break;
            }
        } catch (Exception e) {
            try {
                session.getRemote().sendString(gson.toJson(new ErrorMessage("Error processing your command: " + e.getMessage())));
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}
