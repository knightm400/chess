package service;

import dataAccess.GameDataAccess;
import dataAccess.DataAccessException;
import model.GameData;
import model.AuthData;

public class CreateGameService {
    private GameDataAccess gameDataAccess;

    public CreateGameService(GameDataAccess gameDataAccess) {
        this.gameDataAccess = gameDataAccess;
    }

    public GameData createGame(String authToken, String gameName) throws DataAccessException {
        GameData newGame = new GameData("dummyGameID", "whiteUser","blackUser", gameName); // Set necessary game details
        newGame.setGameName(gameName);
        gameDataAccess.insertGame(newGame);
        return newGame;
    }
}
