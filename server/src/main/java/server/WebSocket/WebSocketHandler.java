package server.WebSocket;

import dataAccess.AuthDataAccess;
import dataAccess.DataAccessException;
import dataAccess.GameDataAccess;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.*;
import com.google.gson.Gson;
import server.WebSocket.Connection;
import server.WebSocket.ConnectionManager;
import webSocketMessages.userCommands.JoinPlayerCommand;
import webSocketMessages.userCommands.JoinObserverCommand;
import webSocketMessages.userCommands.MakeMoveCommand;
import webSocketMessages.userCommands.UserGameCommand;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadGameMessage;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;

import java.util.List;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {
    private static final Gson gson = new Gson();
    private final ConnectionManager connectionManager;
    private final AuthDataAccess authDataAccess;
    private final GameDataAccess gameDataAccess;

    public WebSocketHandler(AuthDataAccess authDataAccess, ConnectionManager connectionManager, GameDataAccess gameDataAccess) {
        this.authDataAccess = authDataAccess;
        this.connectionManager = connectionManager;
        this.gameDataAccess = gameDataAccess;
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        try {
            List<String> tokens = session.getUpgradeRequest().getParameterMap().get("authToken");
            if (tokens == null || tokens.isEmpty()) {
                session.close(new CloseStatus(1008, "Authentication token is missing"));
                return;
            }
            String token = tokens.get(0);
            AuthData authData = authDataAccess.getAuth(token);
            if (authData != null) {
                Connection connection = new Connection(session, authData);
                connectionManager.add(session, connection);
                session.getRemote().sendString(gson.toJson(new GenericMessage("Welcome!")));
            } else {
                session.close(new CloseStatus(1008, "Authentication failed"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (session.isOpen()) {
                try {
                    session.close(new CloseStatus(1011, "An error occurred"));
                } catch (Exception generalException) {
                    generalException.printStackTrace();
                }
            }
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
                    handleJoinPlayer(session, command);
                    break;
                case JOIN_OBSERVER:
                    handleJoinObserver(session, command);
                    break;
                case MAKE_MOVE:
                    handleMakeMove(session, command, message);
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

    private void handleJoinPlayer(Session session, UserGameCommand command) throws IOException {
        JoinPlayerCommand joinCommand = (JoinPlayerCommand) command;
        try {
            GameData gameData = gameDataAccess.getGame(joinCommand.getGameId());
            if (gameData != null) {
                String username = connectionManager.getConnection(session).getAuthData().username();
                session.getRemote().sendString(gson.toJson(new LoadGameMessage(gameData)));
            } else {
                session.getRemote().sendString(gson.toJson(new ErrorMessage("Game not found or access denied")));
            }
        } catch (DataAccessException e) {
            try {
                session.getRemote().sendString(gson.toJson(new ErrorMessage("Database error: " + e.getMessage())));
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    private void handleJoinObserver(Session session, UserGameCommand command) throws IOException {
        if (!(command instanceof JoinObserverCommand)) return;
        JoinObserverCommand observerCommand = (JoinObserverCommand) command;
        try {
            GameData gameData = gameDataAccess.getGame(observerCommand.getGameId());
            if (gameData != null) {
                String username = connectionManager.getConnection(session).getAuthData().username();
                session.getRemote().sendString(gson.toJson(new LoadGameMessage(gameData)));
            } else {
                session.getRemote().sendString(gson.toJson(new ErrorMessage("Game not found")));
            }
        } catch (DataAccessException e) {
            session.getRemote().sendString(gson.toJson(new ErrorMessage("Database error: " + e.getMessage())));
        }
    }

    private void handleMakeMove(Session session, UserGameCommand command, String message) throws IOException {
        if (!(command instanceof MakeMoveCommand)) return;
        MakeMoveCommand moveCommand = (MakeMoveCommand) command;
        try {
            GameData gameData = gameDataAccess.getGame(moveCommand.getGameId());
            String confirmationMessage = "Move received and processed successfully!";
            session.getRemote().sendString(confirmationMessage);
        } catch (DataAccessException e) {
            session.getRemote().sendString(gson.toJson(new ErrorMessage("Database error: " + e.getMessage())));

        }
    }

}
