package dataAccess;

import model.AuthData;
import java.util.List;

public interface AuthDataAccess {
    void insertAuth(AuthData auth) throws DataAccessException;
    AuthData getAuth(String authToken) throws DataAccessException;
    void deleteAuth(String authToken) throws DataAccessException;
    List<AuthData> listAuths() throws DataAccessException;
    void clearAuths() throws DataAccessException;
    String generateAuthToken();
}
