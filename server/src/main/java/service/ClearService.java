package service;

import dataAccess.UserDataAccess;
import dataAccess.GameDataAccess;
import dataAccess.DataAccessException;

public class ClearService {
    private UserDataAccess userDataAccess;
    private GameDataAccess gameDataAccess;

    public ClearService(UserDataAccess userDataAccess, GameDataAccess gameDataAccess) {
        this.userDataAccess = userDataAccess;
        this.gameDataAccess = gameDataAccess;
    }

    public void clearAll() throws DataAccessException {
        userDataAccess.clearDatabase();
        gameDataAccess.clearDatabase();
        // Include other data access clear methods as needed
    }
}
