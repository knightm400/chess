package dataAccess;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AuthDataAccess implements IAuthDataAccess {
    private Map<String, String> authTokens = new HashMap<>();

    @Override
    public String createAuth(String username) throws DataAccessException {
        String authToken = UUID.randomUUID().toString();
        authTokens.put(authToken, username);
        return authToken;
    }

    @Override
    public String getAuth(String authToken) throws DataAccessException {
        return authTokens.get(authToken);
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        authTokens.remove(authToken);
    }
}
