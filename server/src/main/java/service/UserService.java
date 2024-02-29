package service;

import dataAccess.IUserDataAccess;
import model.UserData;
import dataAccess.DataAccessException;
public class UserService {
    private IUserDataAccess dataAccess;
    public UserService(IUserDataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public UserData createUser(String username, String password, String email) throws DataAccessException {
        UserData user = new UserData(username, password, email);
        dataAccess.insertUser(user);
        return user;
    }

    public UserData getUser(String username) throws DataAccessException {
        return dataAccess.getUser(username);
    }
}
