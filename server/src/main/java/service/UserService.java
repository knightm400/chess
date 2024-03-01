package service;

import dataAccess.UserDataAccess;
import model.UserData;
import dataAccess.DataAccessException;

import java.util.List;

public class UserService {
    private UserDataAccess userDataAccess;

    public UserService(UserDataAccess userDataAccess) {
        this.userDataAccess = userDataAccess;
    }

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
