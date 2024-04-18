package server.WebSocket;

import chess.ChessGame;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dataAccess.AuthDataAccess;
import dataAccess.DataAccessException;
import dataAccess.GameDataAccess;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.*;
import com.google.gson.Gson;
import service.JoinGameService;
import service.Result.JoinGameResult;
import webSocketMessages.serverMessages.NotificationMessage;
import webSocketMessages.userCommands.*;
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

import static chess.ChessGame.TeamColor.WHITE;

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
            Connection connection = authenticate(session, genericCommand.getAuthString());
            if (connection == null) {
                session.getRemote().sendString(gson.toJson(new ErrorMessage("Authentication required")));
                return;
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
                    handleMakeMove(session, makeMoveCommand, message);
                    break;
                case LEAVE:
                    LeaveCommand leaveCommand = gson.fromJson(message, LeaveCommand.class);
                    handleLeave(session, leaveCommand);
                case RESIGN:
                    ResignCommand resignCommand = gson.fromJson(message, ResignCommand.class);
                    handleResign(session, resignCommand);
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

    private Connection authenticate(Session session, String authToken) {
        try {
            AuthData authData = authDataAccess.getAuth(authToken);
            if (authData != null) {
                logger.info("Authentication successful for token: " + authToken);
                return new Connection(session, authData, null); // Assuming gameId is not known at this point
            } else {
                session.getRemote().sendString(gson.toJson(new ErrorMessage("Invalid authentication token")));
            }
        } catch (DataAccessException | IOException e) {
            logger.log(Level.SEVERE, "Authentication failed: ", e);
            try {
                session.getRemote().sendString(gson.toJson(new ErrorMessage("Authentication error: " + e.getMessage())));
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "Failed to send error message: ", ex);
            }
        }
        return null;
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
        Connection connection = authenticate(session, joinCommand.getAuthString());
        if (connection == null) {
            return;
        }

        try {
            GameData gameData = gameDataAccess.getGame(joinCommand.getGameId());
            if (gameData == null) {
                session.getRemote().sendString(gson.toJson(new ErrorMessage("Game not found")));
                return;
            }

            if ((joinCommand.getPlayerColor().equals(ChessGame.TeamColor.WHITE) && gameData.whiteUsername() != null) ||
                    (joinCommand.getPlayerColor().equals(ChessGame.TeamColor.BLACK) && gameData.blackUsername() != null)) {
                session.getRemote().sendString(gson.toJson(new ErrorMessage("Color already taken")));
                return;
            }

            updateGameDataWithPlayer(gameData, joinCommand.getPlayerColor(), connection.getAuthData().username());

            connectionManager.add(session, new Connection(session, connection.getAuthData(), joinCommand.getGameId()));
            session.getRemote().sendString(gson.toJson(new LoadGameMessage(gameData)));

            String username = connection.getAuthData().username();
            String message = username + " has joined the game as " + joinCommand.getPlayerColor();
            NotificationMessage notification = new NotificationMessage(message);
            connectionManager.broadcastMessage(joinCommand.getGameId(), gson.toJson(notification), session);
            logger.info("Player " + username + " joined as " + joinCommand.getPlayerColor() + " and notifications sent to other players.");

        } catch (DataAccessException e) {
            logger.log(Level.SEVERE, "Error accessing game data: " + e.getMessage());
            session.getRemote().sendString(gson.toJson(new ErrorMessage("Error processing your request")));
        }
    }

    private void updateGameDataWithPlayer(GameData gameData, ChessGame.TeamColor playerColor, String username) throws DataAccessException {
        GameData updatedGameData;
        if ("WHITE".equals(playerColor)) {
            updatedGameData = new GameData(
                    gameData.gameID(),
                    username,
                    gameData.blackUsername(),
                    gameData.gameName(),
                    gameData.gameData(),
                    "WHITE",
                    gameData.blackColor()
            );
        } else {
            updatedGameData = new GameData(
                    gameData.gameID(),
                    gameData.whiteUsername(),
                    username,
                    gameData.gameName(),
                    gameData.gameData(),
                    gameData.whiteColor(),
                    "BLACK"
            );
        }

        try {
            gameDataAccess.updateGame(updatedGameData);
            logger.info("Game data updated successfully for game ID " + gameData.gameID());
        } catch (DataAccessException e) {
            logger.log(Level.SEVERE, "Failed to update game data: " + e.getMessage());
            throw e;
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

    private void handleLeave(Session session, LeaveCommand leaveCommand) {
        try {
            GameData gameData = gameDataAccess.getGame(leaveCommand.getGameId());
            if (gameData == null) {
                session.getRemote().sendString(gson.toJson(new ErrorMessage("Game not found")));
                return;
            }

            connectionManager.remove(session);

            NotificationMessage notification = new NotificationMessage(leaveCommand.getUsername() + " has left the game.");
            connectionManager.broadcastMessage(leaveCommand.getGameId(), gson.toJson(notification), null); // Notify all clients in the game

            logger.info(leaveCommand.getUsername() + " left game ID " + leaveCommand.getGameId());
        } catch (Exception e) {
            logger.severe("Error processing leave command: " + e.getMessage());
            safelyCloseSession(session, 1011, "Error processing leave command");
        }

    }

    private void handleResign(Session session, ResignCommand resignCommand) {
        try {
            GameData gameData = gameDataAccess.getGame(resignCommand.getGameId());
            if (gameData == null) {
                session.getRemote().sendString(gson.toJson(new ErrorMessage("Game not found")));
                return;
            }

            String winner = gameData.whiteUsername().equals(resignCommand.getUsername()) ? gameData.blackUsername() : gameData.whiteUsername();

            NotificationMessage notification = new NotificationMessage(resignCommand.getUsername() + " has resigned. Winner is " + winner);
            connectionManager.broadcastMessage(resignCommand.getGameId(), gson.toJson(notification), null); // Notify all clients in the game

            logger.info(resignCommand.getUsername() + " resigned from game ID " + resignCommand.getGameId() + ". Winner declared: " + winner);
        } catch (Exception e) {
            logger.severe("Error processing resign command: " + e.getMessage());
            safelyCloseSession(session, 1011, "Error processing resign command");
        }
    }
}