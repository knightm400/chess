package service;

import dataAccess.AuthDataAccess;
import dataAccess.GameDataAccess;
import dataAccess.DataAccessException;
import model.AuthData;
import service.Request.ListGamesRequest;
import service.Result.ListGamesResult;
import model.GameData;
import java.util.List;

public class ListGamesService {

    private final GameDataAccess gameDataAccess;
    private final AuthDataAccess authDataAccess;

    public ListGamesService(GameDataAccess gameDataAccess, AuthDataAccess authDataAccess) {
        this.gameDataAccess = gameDataAccess;
        this.authDataAccess = authDataAccess;
    }

    // I need to pass through an AuthToken for here, verify it.

    public ListGamesResult listGames(String authToken, ListGamesRequest request) throws DataAccessException {
        AuthData authData = authDataAccess.getAuth(authToken);
        if (authData == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        List<GameData> gamesList = gameDataAccess.listGames();
        return new ListGamesResult(gamesList, true, "");
    }
}
