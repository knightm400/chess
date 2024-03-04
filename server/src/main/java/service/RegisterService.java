package service;

import dataAccess.UserDataAccess;
import dataAccess.DataAccessException;
import model.UserData;
import model.AuthData;

public class RegisterService {
    private UserDataAccess userDataAccess;

    public RegisterService(UserDataAccess userDataAccess) {
        this.userDataAccess = userDataAccess;
    }

    public AuthData register(UserData user) throws DataAccessException {
        if (userDataAccess.getUser(user.getUsername()) != null) {
            throw new DataAccessException("User already exists.");
        }

        userDataAccess.insertUser(user);
        String authToken = "generated_auth_token_for_" + user.getUsername();
        return new AuthData(user.getUsername(), authToken);
    }
}
