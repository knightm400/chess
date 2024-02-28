package dataAccess;

import model.UserData;
import java.util.List;
public class IUserDataAccess {
    void insertUser(UserData user) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
    void updateUser(UserData user) throws DataAccessException;
    void deleteUser(String username) throws DataAccessException;
    List<UserData> getAllUsers() throws DataAccessException;
}
