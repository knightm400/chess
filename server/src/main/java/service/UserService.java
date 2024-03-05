package service;

import dataAccess.UserDataAccess;
import dataAccess.AuthDataAccess;
import dataAccess.DataAccessException;
import model.UserData;
import model.AuthData;

public class UserService {
    private final UserDataAccess userDataAccess;
    private final AuthDataAccess authDataAccess;

    public UserService(UserDataAccess userDataAccess, AuthDataAccess authDataAccess) {
        this.userDataAccess = userDataAccess;
        this.authDataAccess = authDataAccess;
    }

    public AuthData register(UserData user) throws DataAccessException {
        if (userDataAccess.getUser(user.username()) != null) {
            throw new DataAccessException("User already exists.");
        }
        userDataAccess.insertUser(user);
        String authToken = authDataAccess.generateAuthToken();
        authDataAccess.insertAuth(new AuthData(authToken, user.username()));
        return new AuthData(authToken, user.username());
    }

    public AuthData login(String username, String password) throws DataAccessException {
        UserData user = userDataAccess.getUser(username);
        if (user == null || !user.password().equals(password)) {
            throw new DataAccessException("Invalid username or password.");
        }
        AuthData authData = authDataAccess.getAuthByUsername(username);
        if (authData == null) {
            String newAuthToken = authDataAccess.generateAuthToken();
            authData = new AuthData(newAuthToken, username);
            authDataAccess.insertAuth(authData);
        }
        return authData;
    }

    public void logout(String authToken) throws DataAccessException {
        AuthData authData = authDataAccess.getAuth(authToken);
        if (authData == null) {
            throw new DataAccessException("Invalid or expired authToken.");
        }
        authDataAccess.deleteAuth(authToken);
    }
}
