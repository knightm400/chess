package dataAccess;

import model.UserData;
import org.eclipse.jetty.server.Authentication;

import java.util.HashMap;
import java.util.Map;

public class MemoryDataAccess implements UserDataAccess {
    private boolean throwDataAccessException = false;
    private Map<String, UserData> userDataMap = new HashMap<>();

    @Override
    public void clearDatabase() throws DataAccessException {
        if (throwDataAccessException) {
            throw new DataAccessException("Simulated DataAccessException");
        }
        userDataMap.clear();
    }

    @Override
    public void insertUser(UserData user) throws DataAccessException {
        userDataMap.put(user.getUsername(), user);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return userDataMap.get(username);
    }

    public void setThrowDataAccessException(boolean throwDataAccessException) {
        this.throwDataAccessException = throwDataAccessException;
    }
}
