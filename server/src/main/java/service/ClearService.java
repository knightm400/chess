package service;

import dataAccess.IUserDataAccess;
import dataAccess.DataAccessException;

public class ClearService {
    private IUserDataAccess userDataAccess;

    public ClearService(IUserDataAccess userDataAccess) {
        this.userDataAccess = userDataAccess;
    }

    public void clearAllData() throws DataAccessException {
        userDataAccess.clearAllData();
    }
}
