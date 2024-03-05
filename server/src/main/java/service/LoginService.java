package service;

import dataAccess.AuthDataAccess;
import dataAccess.DataAccessException;
import dataAccess.UserDataAccess;
import model.AuthData;
import model.UserData;

public class LoginService {
    private UserDataAccess userDataAccess;
    private AuthDataAccess authDataAccess;

    public LoginService(UserDataAccess userDataAccess, AuthDataAccess authDataAccess) {
        this.userDataAccess = userDataAccess;
        this.authDataAccess = authDataAccess;
    }

    public AuthData login(String username, String password) throws DataAccessException {
        UserData user = userDataAccess.getUser(username);
        if (user == null) {
            throw new DataAccessException("User does not exist");
        }
        if (!user.password().equals(password)) {
            throw new DataAccessException("Incorrect password");
        }
        AuthData newAuth = new AuthData(java.util.UUID.randomUUID().toString(), username);
        authDataAccess.insertAuth(newAuth);
        return newAuth;
    }
}
