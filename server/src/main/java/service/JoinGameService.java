package service;

import dataAccess.GameDataAccess;
import model.GameData;

public class JoinGameService {
    private GameDataAccess gameDataAccess;

    public JoinGameService(GameDataAccess gameDataAccess) {
        this.gameDataAccess = gameDataAccess;
    }

    public JoinGameResult joinGame(String authToken, String gameID, String playerColor) {
        GameData gameData = gameDataAccess.getGame(gameID);
        if (gameData != null) {
            return new JoinGameResult(true, "Successfully joined the game");
        } else {
            return new JoinGameResult(false, "Game not found");
        }
    }
}
