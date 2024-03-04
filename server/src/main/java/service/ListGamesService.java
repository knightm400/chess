package service;

import dataAccess.GameDataAccess;
import dataAccess.DataAccessException;
import model.GameData;
import java.util.List;

public class ListGamesService {
    private GameDataAccess gameDataAccess;

    public ListGamesService(GameDataAccess gameDataAccess) {
        this.gameDataAccess = gameDataAccess;
    }

    public List<GameData> listGames(String authToken) throws DataAccessException {
        return gameDataAccess.getAllGames();
    }
}
