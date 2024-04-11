package ui.WebSocket;

import chess.ChessGame;
import com.google.gson.Gson;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.UserGameCommand;
import webSocketMessages.userCommands.JoinPlayerCommand;

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

    public static void main(String[] args) throws Exception {
        WebSocketClient client = new WebSocketClient("ws://localhost:8080/connect");
        client.joinGameAsPlayer("yourAuthTokenHere", 1, ChessGame.TeamColor.WHITE);
    }
}
