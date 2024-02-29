package service;

import dataAccess.DataAccessException;
import dataAccess.IAuthDataAccess;

public class AuthService {
    private IAuthDataAccess authDataAccess;
    public AuthService(IAuthDataAccess authDataAccess) {
        this.authDataAccess = authDataAccess;
    }

    public String createAuth(String username) throws DataAccessException {
        return authDataAccess.createAuth(username);
    }

    public String getAuth(String authToken) throws DataAccessException {
        return authDataAccess.getAuth(authToken);
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        authDataAccess.deleteAuth(authToken);
    }
}
