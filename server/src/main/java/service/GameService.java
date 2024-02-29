package service;

import dataAccess.DataAccessException;
import dataAccess.IGameDataAccess;
import model.GameData;

public class GameService {
    private IGameDataAccess gameDataAccess;
    public GameService(IGameDataAccess gameDataAccess) {
        this.gameDataAccess = gameDataAccess;
    }

    public GameData createGame(String gameName, String username) throws DataAccessException {
        GameData newGame = new GameData(gameName, username);
        gameDataAccess.createGame(newGame);
        return newGame;
    }
}
