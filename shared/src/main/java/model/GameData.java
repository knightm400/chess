package model;

public class GameData {
    private String gameID;
    private String gameName;
    private String gameData;

    public GameData(String gameID, String gameName, String gameData) {
        this.gameID = gameID;
        this.gameName = gameName;
        this.gameData = gameData;
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
