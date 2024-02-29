package service;

import dataAccess.DataAccessException;

public class GameService {
    private IGameDataAccess gameDataAccess;
    public GameService(IGameDataAccess gameDataAccess) {
        this.gameDataAccess = gameDataAccess;
    }

    public GameData createGame(String gameName, String username) throws DataAccessException {
        GameData newGame = new GameData(gameName, username);
        gameDataAccess.insertGame(newGame);
        return newGame;
    }
}