package dataAccess;

import model.AuthData;
import java.util.HashMap;
import java.util.Map;

public class AuthDataAccess {
    private Map<String, AuthData> authTokens = new HashMap<>();

    public void insertAuthData(AuthData auth) {
        authTokens.put(auth.getAuthToken(), auth);
    }

    public AuthData getAuthData(String authToken) {
        return authTokens.get(authToken);
    }

    public void deleteAuthData(String authToken) {
        authTokens.remove(authToken);
    }

    public void clearDatabase() {
        authTokens.clear();
    }
}
