package ui.WebSocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import ui.Gameplay;
import ui.ServerFacade;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.NotificationMessage;
import webSocketMessages.userCommands.*;

import javax.websocket.ClientEndpoint;
import javax.websocket.Endpoint;
import javax.websocket.*;
import java.net.URI;

@ClientEndpoint
public class WebSocketClient extends Endpoint {
    private Session session;
    private final Gson gson = new Gson();
    private final String serverUri;
    private ui.Gameplay gameplay;

    public WebSocketClient(String serverUri, ui.Gameplay gameplay) throws Exception {
        this.serverUri = serverUri;
        this.gameplay = gameplay;
        connect();
    }

    private void connect() throws Exception {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        int retryCount = 0;
        final int MAX_RETRIES = 5;

        while (true) {
            try {
                this.session = container.connectToServer(this, new URI(serverUri));
                this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                    @Override
                    public void onMessage(String message) {
                        handleServerMessage(message);
                    }
                });
                System.out.println("Connected to server");
                break;
            } catch (Exception e) {
                retryCount++;
                if (retryCount > MAX_RETRIES) {
                    System.out.println("Failed to connect after " + MAX_RETRIES + " attempts.");
                    throw e;
                }
                e.printStackTrace();
                System.out.println("Connection failed, retrying in 5 seconds...");
                Thread.sleep(5000);
            }
        }
    }


    @Override
    public void onOpen(Session session, EndpointConfig config) {
        System.out.println("Connected to server");
    }

    private void handleServerMessage(String message) {
        ServerMessage serverMessage = gson.fromJson(message, ServerMessage.class);
        switch(serverMessage.getServerMessageType()) {
            case LOAD_GAME:
                if (gameplay != null) {
                    LoadGameMessage loadGameMessage = gson.fromJson(message, LoadGameMessage.class);
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
        }
    }

    public void sendUserCommand(UserGameCommand command) throws Exception {
        if (this.session == null || !this.session.isOpen()) {
            throw new IllegalStateException("Cannot send message. WebSocket is not connected.");
        }
        String message = gson.toJson(command);
        this.session.getBasicRemote().sendText(message);
    }

    public void joinGameAsPlayer(String authToken, int gameId, ChessGame.TeamColor playerColor) throws Exception {
        JoinPlayerCommand joinCommand = new JoinPlayerCommand(authToken, gameId, playerColor);
        String message = gson.toJson(joinCommand);
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
        ResignCommand resignCommand = new ResignCommand(authToken, gameId);
        String message = gson.toJson(resignCommand);
        this.session.getBasicRemote().sendText(message);
    }

    public void closeConnection() throws Exception {
        if (session != null) {
            session.close();
            System.out.println("WebSocket connection closed.");
        }
    }

    public static void main(String[] args) throws Exception {
        String serverUri = "ws://localhost:8080/connect";
        ServerFacade serverFacade = new ServerFacade();
        Gameplay gameplay = new Gameplay(serverFacade);
        WebSocketClient client = new WebSocketClient(serverUri, gameplay);
        client.joinGameAsPlayer("yourAuthTokenHere", 1, ChessGame.TeamColor.WHITE);
    }
}