package server.WebSocket;

import dataAccess.AuthDataAccess;
import dataAccess.DataAccessException;
import dataAccess.GameDataAccess;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.*;
import com.google.gson.Gson;
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
import java.util.logging.Logger;

@WebSocket
public class WebSocketHandler {
    private static final Gson gson = new Gson();
    private final ConnectionManager connectionManager;
    private final AuthDataAccess authDataAccess;
    private final GameDataAccess gameDataAccess;
    private static final Logger logger = Logger.getLogger(WebSocketHandler.class.getName());

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
                session.getRemote().sendString(gson.toJson("Welcome!"));
            } else {
                session.close(new CloseStatus(1008, "Authentication failed"));
            }
        } catch (Exception e) {
            logger.severe("Error during WebSocket connection: " + e.getMessage());
            safelyCloseSession(session, 1011, "An error occurred");
        }
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        logger.info("Websocket connection closed: " + session.getRemoteAddress().getAddress() + ", Reason: " + reason);
        connectionManager.remove(session);
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        try {
            if (session == null || !session.isOpen()) {
                logger.warning("Session is closed or null, cannot process message");
                return;
            }
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
            logger.severe("Error processing WebSocket message: " + e.getMessage());
            safelyCloseSession(session, 1011, "Error processing your command");
        }
    }

    private void safelyCloseSession(Session session, int statusCode, String reason) {
        if (session != null && session.isOpen()) {
            try {
                session.close(new CloseStatus(statusCode, reason));
            } catch (Exception e) {
                logger.severe("Failed to close session: " + e.getMessage());
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
            session.getRemote().sendString(gson.toJson(new ErrorMessage("Database error: " + e.getMessage())));
        }
    }

    private void handleJoinObserver(Session session, UserGameCommand command) throws IOException {
        JoinObserverCommand observerCommand = (JoinObserverCommand) command;
        try {
            GameData gameData = gameDataAccess.getGame(observerCommand.getGameId());
            if (gameData != null) {
                session.getRemote().sendString(gson.toJson(new LoadGameMessage(gameData)));
            } else {
                session.getRemote().sendString(gson.toJson(new ErrorMessage("Game not found")));
            }
        } catch (DataAccessException e) {
            session.getRemote().sendString(gson.toJson(new ErrorMessage("Database error: " + e.getMessage())));
        }
    }

    private void handleMakeMove(Session session, UserGameCommand command, String message) throws IOException {
        MakeMoveCommand moveCommand = (MakeMoveCommand) command;
        try {
            GameData gameData = gameDataAccess.getGame(moveCommand.getGameId());
            if (gameData != null) {
                session.getRemote().sendString(gson.toJson(new LoadGameMessage(gameData)));
            } else {
                session.getRemote().sendString(gson.toJson(new ErrorMessage("Unable to make move")));
            }
        } catch (DataAccessException e) {
            session.getRemote().sendString(gson.toJson(new ErrorMessage("Database error: " + e.getMessage())));
        }
    }

}
