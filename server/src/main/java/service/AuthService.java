package service;

import dataAccess.UserDataAccess;
import dataAccess.AuthDataAccess;
import model.UserData;
import dataAccess.DataAccessException;
import model.AuthData;

public class AuthService {
    private UserDataAccess userDataAccess;
    private AuthDataAccess authDataAccess;

    public AuthService(UserDataAccess userDataAccess) {
        this.userDataAccess = userDataAccess;
    }

    public AuthData register(UserData user) throws DataAccessException {
        if (userDataAccess.getUser(user.getUsername()) != null) {
            throw new DataAccessException("User already exists.");
        }
        userDataAccess.insertUser(user);
        return new AuthData(user.getUsername(), "generated_auth_token");
    }

    public AuthData login(UserData user) throws DataAccessException {
        UserData foundUser = userDataAccess.getUser(user.getUsername());
        if (foundUser != null && foundUser.getPassword().equals(user.getPassword())) {
            return new AuthData(user.getUsername(), "generated_auth_token");
        } else {
            throw new DataAccessException("Invalid username or password.");
        }
    }

    public void logout(String authToken) throws DataAccessException {
        authDataAccess.deleteAuthData(authToken);
    }

    public boolean validateAuth(String authToken) throws DataAccessException {
        AuthData authData = authDataAccess.getAuthData(authToken);
        return authData != null;
    }

}
