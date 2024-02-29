package model;

public class GameData {
    private String gameName;
    private String username;

    public GameData(String gameName, String username) {
        this.gameName = gameName;
        this.username = username;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
