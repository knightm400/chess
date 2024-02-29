package dataAccess;

import model.UserData;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserDataAccess implements IUserDataAccess {
    private final Connection connection;
    private final Map<String, UserData> users = new HashMap<>();

    public UserDataAccess(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void clearAll() throws DataAccessException {
        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM user");
            statement.executeUpdate();
            statement = connection.prepareStatement("DELETE FROM auth");
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to clear user data" + e.getMessage());
        }
    }

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