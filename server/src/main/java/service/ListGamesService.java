package service;

import dataAccess.GameDataAccess;
import dataAccess.DataAccessException;
import service.ListGamesRequest;
import service.ListGamesResult;
import model.GameData;
import java.util.List;

public class ListGamesService {

    private final GameDataAccess gameDataAccess;

    public ListGamesService(GameDataAccess gameDataAccess) {
        this.gameDataAccess = gameDataAccess;
    }

    public ListGamesResult listGames(ListGamesRequest request) throws DataAccessException {
        List<GameData> gamesList = gameDataAccess.listGames();
        return new ListGamesResult(gamesList, true, ""); 
    }
}
