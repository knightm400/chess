package dataAccess;

import model.UserData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemoryUserDataAccess implements UserDataAccess {
    private final Map<String, UserData> users = new HashMap<>();

    @Override
    public void insertUser(UserData user) throws DataAccessException {
        if (users.containsKey(user.username())) {
            throw new DataAccessException("User already exists.");
        }
        users.put(user.username(), user);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return users.get(username);
    }

    @Override
    public List<UserData> listUsers() {
        return new ArrayList<>(users.values());
    }


    @Override
    public UserData validateUser(String username, String password) throws DataAccessException {
        UserData user = users.get(username);
        if (user != null && user.password().equals(password)) {
            return user;
        } else {
            throw new DataAccessException("Invalid username or password.");
        }
    }

    @Override
    public void clearUsers() {
        users.clear();
    }
}
