package ui.WebSocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import model.GameData;
import ui.Gameplay;
import ui.PostLogin;
import ui.ServerFacade;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.NotificationMessage;
import webSocketMessages.userCommands.*;

import javax.websocket.ClientEndpoint;
import javax.websocket.Endpoint;
import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;

@ClientEndpoint
public class WebSocketClient extends Endpoint {
    private Session session;
    private static volatile WebSocketClient instance;
    private final Gson gson = new Gson();
    private final String serverUri = "ws://localhost:8080/connect";
    private ui.Gameplay gameplay;

    private WebSocketClient() throws Exception {
        connect();
    }


    public static WebSocketClient getInstance() throws Exception {
        if (instance == null) {
            synchronized (WebSocketClient.class) {
                if (instance == null) {
                    instance = new WebSocketClient();
                }
            }
        }
        return instance;
    }

    private synchronized void connect() throws Exception {
        if (session != null && session.isOpen()) {
            System.out.println("Connection is already open.");
            return;
        }

        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        int retryCount = 0;
        final int MAX_RETRIES = 5;

        while (session == null || !session.isOpen()) {
            try {
                session = container.connectToServer(this, new URI(serverUri));
                session.addMessageHandler(String.class, this::handleServerMessage);
                break;
            } catch (Exception e) {
                retryCount++;
                if (retryCount > MAX_RETRIES) {
                    System.out.println("Failed to connect after " + MAX_RETRIES + " attempts.");
                    throw new Exception("Unable to establish WebSocket connection.");
                }
                System.out.println("Connection attempt failed, retrying...");
                Thread.sleep(5000);
            }
        }
    }


    @Override
    public void onOpen(Session session, EndpointConfig config) {
        System.out.println("Connected to server");
        this.session = session;
    }

    public Session getSession() {
        return this.session;
    }

    private void handleServerMessage(String message) {
        try {
            System.out.println("Received message: " + message);
            ServerMessage serverMessage = gson.fromJson(message, ServerMessage.class);
            System.out.println("Parsed message type: " + serverMessage.getServerMessageType()); // Log message type

            switch(serverMessage.getServerMessageType()) {
                case LOAD_GAME:
                    LoadGameMessage loadGameMessage = gson.fromJson(message, LoadGameMessage.class);
                    if (gameplay != null) {
                        gameplay.updateGameFromServer(loadGameMessage.getGame());
                        gameplay.drawChessboard();
                    } else {
                        System.out.println("Gameplay instance not available, cannot update game.");
                    }
                    break;
                case ERROR:
                    ErrorMessage errorMessage = gson.fromJson(message, ErrorMessage.class);
                    System.out.println("Error: " + errorMessage.getErrorMessage());
                    break;
                case NOTIFICATION:
                    NotificationMessage notificationMessage = gson.fromJson(message, NotificationMessage.class);
                    System.out.println("Notification: " + notificationMessage.getMessage());
                    break;
                default:
                    System.out.println("Unknown message type received.");
                    break;
            }
        } catch (JsonSyntaxException e) {
            System.err.println("Failed to parse the message: " + e.getMessage());
        }
    }

    public void joinGameAsPlayer(String authToken, int gameId, ChessGame.TeamColor playerColor) throws Exception {
        JoinPlayerCommand joinCommand = new JoinPlayerCommand(authToken, gameId, playerColor);
        String message = gson.toJson(joinCommand);
        System.out.println("Sending join game command: " + message);
        this.session.getBasicRemote().sendText(message);
    }

    public void joinGameAsObserver(String authToken, int gameId) throws Exception {
        JoinObserverCommand observerCommand = new JoinObserverCommand(authToken, gameId);
        String message = gson.toJson(observerCommand);
        this.session.getBasicRemote().sendText(message);
    }

    public void makeMove(String authToken, int gameId, ChessMove move) throws Exception {
        MakeMoveCommand moveCommand = new MakeMoveCommand(authToken, gameId, move);
        String message = gson.toJson(moveCommand);
        this.session.getBasicRemote().sendText(message);
    }

    public void leaveGame(String authToken, int gameId) throws Exception {
        LeaveCommand leaveCommand = new LeaveCommand(authToken, gameId);
        String message = gson.toJson(leaveCommand);
        this.session.getBasicRemote().sendText(message);
    }

    public void resignGame(String authToken, int gameId) throws Exception {
    }

    public void closeConnection() throws Exception {
        if (session != null) {
            session.close();
            System.out.println("WebSocket connection closed.");
        }
    }
    public static void main(String[] args) throws Exception {
        ServerFacade serverFacade = new ServerFacade();
        PostLogin postLogin = new PostLogin(serverFacade);
        if (serverFacade.getAuthToken() == null || serverFacade.getAuthToken().isEmpty()) {
            System.out.println("Auth token is not available. Please login first.");
            return;
        }
        postLogin.displayMenu();
    }
}