package model;

import java.util.HashSet;
import java.util.Set;

public class GameData {
    private String gameID;
    private String whiteUsername;
    private String blackUsername;
    private String gameName;
    private String gameData;
    private String whiteColor;
    private String blackColor;
    private Set<String> takenColors;

    public GameData(String gameID, String whiteUsername, String blackUsername, String gameName) {
        this.gameID = gameID;
        this.whiteUsername = whiteUsername;
        this.blackUsername = blackUsername;
        this.gameName = gameName;
        this.takenColors = new HashSet<>();
    }

    public String getGameID() {
        return gameID;
    }

    public void setGameID(String gameID) {
        this.gameID = gameID;
    }

    public String getWhiteUsername() {
        return whiteUsername;
    }

    public void setWhiteUsername(String whiteUsername) {
        this.whiteUsername = whiteUsername;
    }

    public String getBlackUsername() {
        return blackUsername;
    }

    public void setBlackUsername(String blackUsername) {
        this.blackUsername = blackUsername;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getGameData() {
        return gameData;
    }

    public void setGameData(String gameData) {
        this.gameData = gameData;
    }

    public String getWhiteColor() {
        return whiteColor;
    }

    public void setWhiteColor(String whiteColor) {
        this.whiteColor = whiteColor;
    }

    public String getBlackColor() {
        return blackColor;
    }

    public void setBlackColor(String blackColor) {
        this.blackColor = blackColor;
    }

    public boolean isColorAvailable(String color) {
        return !takenColors.contains(color);
    }

    public void setPlayerColor(String username, String color) {
        if (whiteUsername.equals(username)) {
            takenColors.remove(whiteColor);
            whiteColor = color;
            takenColors.add(whiteColor);
        } else if (blackUsername.equals(username)) {
            takenColors.remove(blackColor);
            blackColor = color;
            takenColors.add(blackColor);
        }
    }
}

