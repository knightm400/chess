package dataAccess;

import model.UserData;
import java.util.List;

public interface UserDataAccess {
    void insertUser(UserData user) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
    void updateUser(UserData user) throws DataAccessException;
    void deleteUser(String username) throws DataAccessException;
    List<UserData> listUsers() throws DataAccessException;
    UserData validateUser(String username, String password) throws DataAccessException;
}
