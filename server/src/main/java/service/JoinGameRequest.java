package service;

public class JoinGameRequest {
    private String authToken;
    private String gameID;
    private String clientColor;

    public JoinGameRequest(String authToken, String gameID, String clientColor) {
        this.authToken = authToken;
        this.gameID = gameID;
        this.clientColor = clientColor;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getGameID() {
        return gameID;
    }

    public void setGameID(String gameID) {
        this.gameID = gameID;
    }

    public String getClientColor() {
        return clientColor;
    }

    public void setClientColor(String clientColor) {
        this.clientColor = clientColor;
    }
}
