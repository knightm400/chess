package service;

import dataAccess.UserDataAccess;
import dataAccess.GameDataAccess;
import dataAccess.AuthDataAccess;
import dataAccess.DataAccessException;
import service.Result.ClearResult;

public class ClearService {
    private final UserDataAccess userDataAccess;
    private final GameDataAccess gameDataAccess;
    private final AuthDataAccess authDataAccess;

    public ClearService(UserDataAccess userDataAccess, GameDataAccess gameDataAccess, AuthDataAccess authDataAccess) {
        this.userDataAccess = userDataAccess;
        this.gameDataAccess = gameDataAccess;
        this.authDataAccess = authDataAccess;
    }

    public ClearResult clearAll() throws DataAccessException {
        userDataAccess.clearUsers();
        gameDataAccess.clearGames();
        authDataAccess.clearAuths();
        return new ClearResult("All data cleared");
    }
}
