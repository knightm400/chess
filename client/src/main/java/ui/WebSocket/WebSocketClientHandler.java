package ui.WebSocket;

import server.WebSocket.ConnectionManager;

import javax.websocket.Session;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.websocket.*;
public class WebSocketClientHandler {
    private final ConnectionManager connectionManager;
    private static final Logger logger = Logger.getLogger(WebSocketClientHandler.class.getName());

    public WebSocketClientHandler(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    public void handleNewConnection(Session session) {
        logger.info("New WebSocket connection established: " + session.getId());
    }

    public void handleMessage(String message, Session session) {
        logger.info("Received message from " + session.getId() + ": " + message);
        try {
            processMessage(message, session);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error processing message", e);
            try {
                session.getBasicRemote().sendText("Error processing message: " + e.getMessage());
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "Failed to send error message back to client", ex);
            }
        }
    }

    private void processMessage(String message, Session session) throws IOException {
        connectionManager.sendMessage(session, "Echo: " + message);
    }

    public void handleClose(Session session, int statusCode, String reason) {
        logger.info("WebSocket connection closed: " + session.getId() + " [Status: " + statusCode + ", Reason: " + reason + "]");
        connectionManager.remove(session);
    }

    public void handleError(Session session, Throwable throwable) {
        logger.log(Level.SEVERE, "WebSocket error on connection " + session.getId(), throwable);
    }
}
