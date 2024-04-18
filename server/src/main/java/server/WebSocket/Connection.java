package server.WebSocket;
import model.AuthData;
import org.eclipse.jetty.websocket.api.Session;

public class Connection {
    private final Session session;
    private AuthData authData;
    private final Integer gameId;

    public Connection(Session session, AuthData authData, Integer gameId) {
        this.session = session;
        this.authData = authData;
        this.gameId = gameId;
    }

    public Session getSession() {
        return session;
    }

    public AuthData getAuthData() {
        return authData;
    }

    public Integer getGameId() {
        return gameId;
    }

}
