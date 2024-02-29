package model;

public class GameData {
    private String gameID;
    private String gameName;
    private String gameData;
    private String username;

    public GameData(String gameName, String username) {
        this.gameName = gameName;
        this.username = username;
    }

    public String getGameID() {
        return gameID;
    }

    public void setGameID(String gameID) {
        this.gameID = gameID;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public void setGameData(String gameData) {
        this.gameData = gameData;
    }

    public String getGameData() {
        return gameData;
    }
}
