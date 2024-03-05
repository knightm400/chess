package service;

import dataAccess.AuthDataAccess;
import dataAccess.DataAccessException;
import dataAccess.UserDataAccess;
import model.AuthData;
import model.UserData;

public class AuthService {

    private final UserDataAccess userDataAccess;
    private final AuthDataAccess authDataAccess;

    public AuthService(UserDataAccess userDataAccess, AuthDataAccess authDataAccess) {
        this.userDataAccess = userDataAccess;
        this.authDataAccess = authDataAccess;
    }

    public AuthData register(UserData user) throws DataAccessException {
        if (userDataAccess.getUser(user.username()) != null) {
            throw new DataAccessException("User already exists.");
        }

        userDataAccess.insertUser(user);

        String authToken = authDataAccess.generateAuthToken();;

        authDataAccess.insertAuth(new AuthData(authToken, user.username()));

        return new AuthData(authToken, user.username());
    }

    public void logout(String authToken) throws DataAccessException {
        if (authDataAccess.getAuth(authToken) == null) {
            throw new DataAccessException("Invalid or expired authToken.");
        }

        authDataAccess.deleteAuth(authToken);
    }
}
