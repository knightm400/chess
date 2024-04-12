package server.WebSocket;

import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;
import com.google.gson.Gson;
import server.WebSocket.Connection;
import server.WebSocket.ConnectionManager;
import webSocketMessages.userCommands.UserGameCommand;

import java.io.IOException;
import java.util.List;

@WebSocket
public class WebSocketHandler {
    private static final Gson gson = new Gson();
    private final ConnectionManager connectionManager = new ConnectionManager();

    @OnWebSocketConnect
    public void onConnect(Session session) throws IOException{
        System.out.println("New connection: " + session.getRemoteAddress().getAddress());
        String token = session.getUpgradeRequest().getParameterMap().getOrDefault("authToken", List.of("")).get(0);
        if (token.isEmpty() || !validateAuthToken(token)) {
            session.close(new CloseStatus(1008, "Authentication failed"));
            return;
        }
        Connection connection = new Connection(session, "defaultUsername", token);
        connectionManager.add(session, connection);
    }

    private boolean validateAuthToken(String authToken) {
        return true;
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        System.out.println("Closed connection: " + session.getRemoteAddress().getAddress() + ", Reason: " + reason);
        connectionManager.remove(session);
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        System.out.println("Message from " + session.getRemoteAddress().getAddress() + ": " + message);
        try {
            UserGameCommand command = gson.fromJson(message, UserGameCommand.class);
            processCommand(session, command);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                sendMessage(session, "Error processing your command");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    private void processCommand(Session session, UserGameCommand command) throws IOException {
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
                System.out.println("Unknown command received: " + command.getCommandType());
                break;
        }
    }

    private void sendMessage(Session session, String message) throws IOException {
        session.getRemote().sendString(message);
    }

}