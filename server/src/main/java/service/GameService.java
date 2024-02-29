package service;

import dataAccess.DataAccessException;
import dataAccess.IGameDataAccess;
import model.GameData;

import java.util.List;

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

    public GameData getGame(String gameID) throws DataAccessException {
        return gameDataAccess.getGame(gameID);
    }

    public List<GameData> listGames() throws DataAccessException {
        return gameDataAccess.listGames();
    }

    public void updateGame(String gameID, GameData game) throws DataAccessException {
        gameDataAccess.updateGame(gameID, game);
    }
}
