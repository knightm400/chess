package server.WebSocket;
import org.eclipse.jetty.websocket.api.Session;

public class Connection {
    private final Session session;
    private String username;
    private Integer gameId;
    private String role;
    private String authToken;

    public Connection(Session session, String username, String authToken) {
        this.session = session;
        this.username = username;
        this.gameId = null;
        this.role = null;
        this.authToken = authToken;
    }

    public Session getSession() {
        return session;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getGameId() {
        return gameId;
    }

    public void setGameId(Integer gameId) {
        this.gameId = gameId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
