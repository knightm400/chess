package service;

import dataAccess.UserDataAccess;
import model.AuthData;
import model.UserData;
import dataAccess.DataAccessException;

public class LoginService {
    private UserDataAccess userDataAccess;

    public LoginService(UserDataAccess userDataAccess) {
        this.userDataAccess = userDataAccess;
    }

    public AuthData login(String username, String password) throws DataAccessException {
        UserData user = userDataAccess.getUser(username);
        if (user != null && user.getPassword().equals(password)) {
            return new AuthData(username, "generated_auth_token");
        } else {
            throw new DataAccessException("Invalid username or password.");
        }
    }
}
