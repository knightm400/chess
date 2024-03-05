package service;

import dataAccess.AuthDataAccess;
import dataAccess.DataAccessException;
import service.*;

public class LogoutService {
    private final AuthDataAccess authDataAccess;

    public LogoutService(AuthDataAccess authDataAccess) {
        this.authDataAccess = authDataAccess;
    }

    public void logout(LogoutRequest request) throws DataAccessException {
        authDataAccess.deleteAuth(request.authToken());
    }
}
