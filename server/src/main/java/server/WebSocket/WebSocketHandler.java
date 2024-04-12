package server.WebSocket;

import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;
import com.google.gson.Gson;
import server.WebSocket.Connection;
import server.WebSocket.ConnectionManager;
import webSocketMessages.userCommands.UserGameCommand;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {
    private static final Gson gson = new Gson();
    private final ConnectionManager connectionManager = new ConnectionManager();

    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.println("New connection: " + session.getRemoteAddress().getAddress());
        Connection connection = new Connection(session, "defaultUsername");
        connectionManager.add(session, connection);
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
        } catch (Exception e) {
            e.printStackTrace();
            try {
                session.getRemote().sendString("Error processing your command");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

}