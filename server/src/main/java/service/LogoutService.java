package service;

import dataAccess.AuthDataAccess;
import dataAccess.DataAccessException;

public class LogoutService {
    private AuthDataAccess authDataAccess;

    public LogoutService(AuthDataAccess authDataAccess) {
        this.authDataAccess = authDataAccess;
    }

    public void logout(String authToken) throws DataAccessException {
        authDataAccess.deleteAuthData(authToken);
    }
}
