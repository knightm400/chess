package service;

import dataAccess.UserDataAccess;
import model.UserData;
import dataAccess.DataAccessException;
import model.AuthData;

public class AuthService {
    private UserDataAccess userDataAccess;

    public AuthService(UserDataAccess userDataAccess) {
        this.userDataAccess = userDataAccess;
    }

    public AuthData register(UserData user) throws DataAccessException {
        // Check if user already exists
        if (userDataAccess.getUser(user.getUsername()) != null) {
            throw new DataAccessException("User already exists.");
        }
        // Insert new user
        userDataAccess.insertUser(user);
        // Return new auth token (this should be generated appropriately)
        return new AuthData(user.getUsername(), "generated_auth_token");
    }

    public AuthData login(UserData user) throws DataAccessException {
        UserData foundUser = userDataAccess.getUser(user.getUsername());
        if (foundUser != null && foundUser.getPassword().equals(user.getPassword())) {
            // Return auth token if login is successful
            return new AuthData(user.getUsername(), "generated_auth_token");
        } else {
            throw new DataAccessException("Invalid username or password.");
        }
    }

    public void logout(String username) throws DataAccessException {
        // This method could invalidate the user's auth token if implemented
        // For now, it's just a placeholder to match the structure.
    }
}
