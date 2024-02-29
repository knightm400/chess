package service;

import dataAccess.IAuthDataAccess;
import dataAccess.IUserDataAccess;
import model.UserData;
import dataAccess.DataAccessException;
import model.AuthData;

public class UserService {
    private IUserDataAccess userDataAccess;
    private IAuthDataAccess authDataAccess;
    public UserService(IUserDataAccess userDataAccess, IAuthDataAccess authDataAccess) {
        this.userDataAccess = userDataAccess;
        this.authDataAccess = authDataAccess;
    }

    public UserData createUser(String username, String password, String email) throws DataAccessException {
        UserData user = new UserData(username, password, email);
        userDataAccess.insertUser(user);
        return user;
    }

    public UserData getUser(String username) throws DataAccessException {
        return userDataAccess.getUser(username);
    }

    public String login(String username, String password) throws DataAccessException {
        UserData user = userDataAccess.getUser(username);
        if (user != null && user.getPassword().equals(password)) {
            String authToken = generateAuthToken(username);
            return authToken;
        } else {
            return null;
        }
    }

    private String generateAuthToken(String username) throws DataAccessException {
        String authToken = "generatedAuthToken";
        authDataAccess.createAuth(new AuthData(authToken, username));
        return authToken;
    }
}
