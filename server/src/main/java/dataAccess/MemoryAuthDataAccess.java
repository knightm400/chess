package dataAccess;

import model.AuthData;

import java.util.*;

public class MemoryAuthDataAccess implements AuthDataAccess {
    private final Map<String, AuthData> authTokens = new HashMap<>();

    @Override
    public void insertAuth(AuthData auth) throws DataAccessException {
        if (authTokens.containsKey(auth.authToken())) {
            throw new DataAccessException("Auth token already exists.");
        }
        authTokens.put(auth.authToken(), auth);
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return authTokens.get(authToken);
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        if (!authTokens.containsKey(authToken)) {
            throw new DataAccessException("Auth token does not exist.");
        }
        authTokens.remove(authToken);
    }

    @Override
    public List<AuthData> listAuths() {
        return new ArrayList<>(authTokens.values());
    }


    @Override
    public void clearAuths() {
        this.authTokens.clear();
    }


    @Override
    public AuthData getAuthByUsername(String username) throws DataAccessException {
        return authTokens.values().stream()
                .filter(authData -> authData.username().equals(username))
                .findFirst()
                .orElse(null);
    }

    @Override
    public String generateAuthToken() {
        return UUID.randomUUID().toString();
    }

}