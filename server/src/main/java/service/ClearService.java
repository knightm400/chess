package service;

import dataAccess.DataAccessException;

public class ClearService {
    private IUserDataAccess userDataAccess;

    public ClearService(IUserDataAccess userDataAccess) {
        this.userDataAccess = userDataAccess;
    }

    public void clearAll() throws DataAccessException {
        userDataAccess.clearDatabase();
    }
}
