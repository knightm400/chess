package ui.WebSocket;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.*;
public class WebSocketClientHandler {
    private Session session;
    private static final Logger logger = Logger.getLogger(WebSocketClientHandler.class.getName());

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        logger.info("New WebSocket connection established: " + session.getId());
    }

    public void sendMessage(String message) throws IOException {
        if (session != null && session.isOpen()) {
            session.getBasicRemote().sendText(message);
        } else {
            logger.warning("WebSocket connection is not open.");
            throw new IOException("WebSocket connection is not open.");
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        logger.info("Received message from " + session.getId() + ": " + message);
        processMessage(message);
    }

    private void processMessage(String message) {
        logger.info("Processing message: " + message);
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        logger.info("WebSocket connection closed: " + session.getId() + " [Status: " + closeReason.getCloseCode() + ", Reason: " + closeReason.getReasonPhrase() + "]");
        this.session = null;
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        logger.log(Level.SEVERE, "WebSocket error on connection " + session.getId(), throwable);
    }
}
