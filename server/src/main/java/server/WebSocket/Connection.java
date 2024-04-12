package server.WebSocket;
import model.AuthData;
import org.eclipse.jetty.websocket.api.Session;
import model.UserData;

public class Connection {
    private final Session session;
    private AuthData authData;

    public Connection(Session session, AuthData authData) {
        this.session = session;
        this.authData = authData;
    }

    public Session getSession() {
        return session;
    }

    public AuthData getAuthData() {
        return authData;
    }

    public void setUserData(AuthData authData) {
        this.authData = authData;
    }
}
