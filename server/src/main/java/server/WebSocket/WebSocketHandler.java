package server.WebSocket;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
import java.util.logging.Level;
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

    //Should not set authToken here, should set it in UserGameCommand
    @OnWebSocketConnect
    public void onConnect(Session session) {
        logger.info("WebSocket connection established");
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        logger.info("Websocket connection closed: " + session.getRemoteAddress().getAddress() + ", Reason: " + reason);
        connectionManager.remove(session);
    }

    // Keep original string for serialization
    // After you check what string it is, you need to deserialize to get the original string back
    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        logger.info("Received message: " + message);
        try {
            UserGameCommand genericCommand = gson.fromJson(message, UserGameCommand.class);
            if (!authenticate(session, genericCommand.getAuthString())) {
                session.getRemote().sendString(gson.toJson(new ErrorMessage("Authentication required")));
            }
            switch (genericCommand.getCommandType()) {
                case JOIN_PLAYER:
                    JoinPlayerCommand joinPlayerCommand = gson.fromJson(message, JoinPlayerCommand.class);
                    handleJoinPlayer(session, joinPlayerCommand);
                    break;
                case JOIN_OBSERVER:
                    JoinObserverCommand joinObserverCommand = gson.fromJson(message, JoinObserverCommand.class);
                    handleJoinObserver(session, joinObserverCommand);
                    break;
                case MAKE_MOVE:
                    MakeMoveCommand makeMoveCommand = gson.fromJson(message, MakeMoveCommand.class);
                    break;
                // handle other cases
                default:
                    session.getRemote().sendString(gson.toJson(new ErrorMessage("Unknown command")));
                    break;
            }
        } catch (Exception e) {
            logger.severe("Error processing WebSocket message: " + e.getMessage());
            safelyCloseSession(session, 1011, "Error processing your command");
        }
    }

    private boolean authenticate(Session session, String authToken) {
        try {
            AuthData authData = authDataAccess.getAuth(authToken);
            if (authData != null) {
                Connection connection = new Connection(session, authData);
                connectionManager.add(session, connection);
                logger.info("Authentication successful for token: " + authToken);
                return true;
            } else {
                logger.warning("Failed to authenticate with token: " + authToken);
                try {
                    session.getRemote().sendString(gson.toJson(new ErrorMessage("Invalid authentication token")));
                } catch (IOException e) {
                    logger.severe("Failed to send authentication error message: " + e.getMessage());
                }
                return false;
            }
        } catch (DataAccessException e) {
            logger.severe("Authentication failed: " + e.getMessage());
            try {
                session.getRemote().sendString(gson.toJson(new ErrorMessage("Authentication error: " + e.getMessage())));
            } catch (IOException ioException) {
                logger.severe("Failed to send error message: " + ioException.getMessage());
            }
            return false;
        }
    }

    // Deserialize J
//    private void processCommand(Session session, UserGameCommand command, String message) throws IOException {
//        switch (command.getCommandType()) {
//            case JOIN_PLAYER:
//                JoinPlayerCommand joinPlayerCommand = gson.fromJson(message, JoinPlayerCommand.class);
//                handleJoinPlayer(session, joinPlayerCommand);
//                break;
//            case JOIN_OBSERVER:
//                break;
//            case MAKE_MOVE:
//                break;
//            default:
//                session.getRemote().sendString(gson.toJson(new ErrorMessage("Unknown command")));
//                break;
//        }
//    }

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
        try {
            JoinPlayerCommand joinCommand = (JoinPlayerCommand) command;
            GameData gameData = gameDataAccess.getGame(joinCommand.getGameId());
            if (gameData == null) {
                logger.warning("Game ID " + joinCommand.getGameId() + " not found.");
                session.getRemote().sendString(gson.toJson(new ErrorMessage("Game not found")));
                return;
            }
            LoadGameMessage loadGameMessage = new LoadGameMessage(gameData);
            session.getRemote().sendString(gson.toJson(loadGameMessage));
            logger.info("Loaded game and sent LoadGameMessage for game ID " + joinCommand.getGameId());
        } catch (DataAccessException e) {
            logger.log(Level.SEVERE, "Database access error when attempting to join game: ", e);
            try {
                session.getRemote().sendString(gson.toJson(new ErrorMessage("Error accessing game data")));
            } catch (IOException ioException) {
                logger.log(Level.SEVERE, "Failed to send error message: ", ioException);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "IO Exception when sending game data: ", e);
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
