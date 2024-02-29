package dataAccess;

import model.UserData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UserDataAccess implements IUserDataAccess {
    private final Map<String, UserData> users = new HashMap<>();

    public void clearAllData() {
        users.clear();
    }

    @Override
    public void clearAllGames() throws DataAccessException{}

    public void clearAllUsers() throws DataAccessException{}
    public void clearAllAuthTokens() throws DataAccessException{}
    public void insertUser() throws DataAccessException{}

    @Override
    public void insertUser(UserData user) throws DataAccessException {
        if (users.containsKey(user.getUsername())) {
            throw new DataAccessException("User already exists.");
        }
        users.put(user.getUsername(), user);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return users.get(username);
    }

    @Override
    public void updateUser(UserData user) throws DataAccessException {
        if (!users.containsKey(user.getUsername())) {
            throw new DataAccessException("User does not exist.");
        }
        users.put(user.getUsername(), user);
    }

    @Override
    public void deleteUser(String username) throws DataAccessException {
        if (!users.containsKey(username)) {
            throw new DataAccessException("User does not exist.");
        }
        users.remove(username);
    }

    @Override
    public List<UserData> getAllUsers() {
        return new ArrayList<>(users.values());
    }
}
