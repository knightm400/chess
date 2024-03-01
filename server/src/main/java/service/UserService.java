package service;

import dataAccess.UserDataAccess;
import model.UserData;
import dataAccess.DataAccessException;
import model.AuthData;
import java.util.List;

public class UserService {
    private UserDataAccess userDataAccess;

    public UserService(UserDataAccess userDataAccess) {
        this.userDataAccess = userDataAccess;
    }
    public AuthData register(UserData user) throws DataAccessException {
        UserData existingUser = userDataAccess.getUser(user.getUsername());
        if (existingUser != null) {
            throw new DataAccessException("Registration failed: User already exists.");
        }
        userDataAccess.insertUser(user);
        return new AuthData(user.getUsername(), "example_auth_token");
    }

    public AuthData login(UserData user) throws DataAccessException {
        UserData retrievedUser = userDataAccess.getUser(user.getUsername());
        if (retrievedUser != null && retrievedUser.getPassword().equals(user.getPassword())) {
            return new AuthData(user.getUsername(), "example_auth_token");
        } else {
            throw new DataAccessException("Login failed: Invalid username or password.");
        }
    }

    public void logout(UserData user) throws DataAccessException{}

    public void addUser(UserData user) throws DataAccessException {
        userDataAccess.insertUser(user);
    }

    public UserData getUser(String username) throws DataAccessException {
        return userDataAccess.getUser(username);
    }

    public void deleteUser(String username) throws DataAccessException {
        userDataAccess.deleteUser(username);
    }

    public List<UserData> listUsers() throws DataAccessException {
        return userDataAccess.getAllUsers();
    }

    public void clearAllUsers() throws DataAccessException {
        userDataAccess.clearDatabase();
    }
}
