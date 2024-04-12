package ui.WebSocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.*;

import javax.websocket.ClientEndpoint;
import javax.websocket.Endpoint;
import javax.websocket.*;
import java.net.URI;

@ClientEndpoint
public class WebSocketClient extends Endpoint {
    private Session session;
    private final Gson gson = new Gson();

    public WebSocketClient(String serverUri) throws Exception {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, new URI(serverUri));
        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String message) {
                handleServerMessage(message);
            }
        });
    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {
        System.out.println("Connected to server");
    }

    public void sendUserCommand(UserGameCommand command) throws Exception {
        String message = gson.toJson(command);
        this.session.getBasicRemote().sendText(message);
    }

    private void handleServerMessage(String message) {
        ServerMessage serverMessage = gson.fromJson(message, ServerMessage.class);
        switch(serverMessage.getServerMessageType()) {
            case LOAD_GAME:
                break;
            case ERROR:
                break;
            case NOTIFICATION:
                break;
        }
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
        WebSocketClient client = new WebSocketClient("ws://localhost:8080/connect");
        client.joinGameAsPlayer("yourAuthTokenHere", 1, ChessGame.TeamColor.WHITE);
    }
}