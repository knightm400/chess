package service;

import dataAccess.AuthDataAccess;
import dataAccess.DataAccessException;
import model.AuthData;
import service.Request.LogoutRequest;

public class LogoutService {
    private final AuthDataAccess authDataAccess;

    public LogoutService(AuthDataAccess authDataAccess) {
        this.authDataAccess = authDataAccess;
    }

    public void logout(String authToken, LogoutRequest request) throws DataAccessException {
        AuthData authData = authDataAccess.getAuth(authToken);
        if (authData == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        authDataAccess.deleteAuth(authToken);
    }
}
