package dataAccess;

import model.GameData;
import model.UserData;

import javax.xml.crypto.Data;
import java.util.List;
public interface IUserDataAccess {
    void clearDatabase() throws DataAccessException;
    void insertUser(UserData user) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
    void updateUser(UserData user) throws DataAccessException;
    void deleteUser(String username) throws DataAccessException;
    List<UserData> getAllUsers() throws DataAccessException;
    GameData getGame(String gameID) throws DataAccessException;
    void updateGame(String gameID, GameData game) throws DataAccessException;
}
