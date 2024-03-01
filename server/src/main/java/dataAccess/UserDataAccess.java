package dataAccess;

import model.GameData;
import model.UserData;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserDataAccess{
    private Map<String, UserData> users = new HashMap<>();
    public void clearDatabase() {
        users.clear();
    }

    public void insertUser(UserData user) {
        users.put(user.getUsername(), user);
    }

    public UserData getUser(String username) {
        return users.get(username);
    }

    public void updateUser(UserData user) {
        users.put(user.getUsername(), user);
    }

    public void deleteUser(String username) {
        users.remove(username);
    }

    public List<UserData> getAllUsers() {
        return new ArrayList<>(users.values());
    }
    public boolean isDatabaseEmpty() {
        return users.isEmpty();
    }
}