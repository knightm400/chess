package service;

import dataAccess.GameDataAccess;
import model.GameData;
import dataAccess.DataAccessException;

import java.util.List;

public class GameService {
    private GameDataAccess gameDataAccess;

    public GameService(GameDataAccess gameDataAccess) {
        this.gameDataAccess = gameDataAccess;
    }

    public void createGame(GameData game) throws DataAccessException {
        gameDataAccess.insertGame(game);
    }

    public GameData getGame(String gameID) throws DataAccessException {
        return gameDataAccess.getGame(gameID);
    }

    public void updateGame(String gameID, GameData game) throws DataAccessException {
        gameDataAccess.updateGame(gameID, game);
    }

    public void deleteGame(String gameID) throws DataAccessException {
        gameDataAccess.deleteGame(gameID);
    }

    public List<GameData> listAllGames() throws DataAccessException {
        return gameDataAccess.getAllGames();
    }

}
