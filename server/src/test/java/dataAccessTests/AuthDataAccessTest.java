package dataAccessTests;

import dataAccess.AuthDataAccess;
import dataAccess.DataAccessException;
import dataAccess.MemoryAuthDataAccess;
import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class AuthDataAccessTest {

    private AuthDataAccess authDataAccess;

    @BeforeEach
    public void setUp() {
        authDataAccess = new MemoryAuthDataAccess();
    }

    @Test
    public void insertAndGetAuthSuccess() throws DataAccessException {
        String token = "testToken";
        AuthData newAuth = new AuthData(token, "testUser");
        authDataAccess.insertAuth(newAuth);
        AuthData retrievedAuth = authDataAccess.getAuth(token);
        assertNotNull(retrievedAuth);
        assertEquals("testUser", retrievedAuth.username());
    }

    @Test
    public void insertAuthFailure() {
        String token = "testToken";
        AuthData newAuth = new AuthData(token, "testUser");
        assertThrows(DataAccessException.class, () -> {
            authDataAccess.insertAuth(newAuth);
            authDataAccess.insertAuth(newAuth);
        });
    }

    @Test
    public void deleteAuthSuccess() throws DataAccessException {
        String token = "testToken";
        AuthData newAuth = new AuthData(token, "testUser");
        authDataAccess.insertAuth(newAuth);
        authDataAccess.deleteAuth(token);
        assertNull(authDataAccess.getAuth(token));
    }

    @Test
    public void deleteAuthFailure() {
        assertThrows(DataAccessException.class, () -> authDataAccess.deleteAuth("nonexistentToken"));
    }

    @Test
    public void listAuthsSuccess() throws DataAccessException {
        String token1 = "testToken1";
        String token2 = "testToken2";
        authDataAccess.insertAuth(new AuthData(token1, "testUser1"));
        authDataAccess.insertAuth(new AuthData(token2, "testUser2"));
        List<AuthData> authList = authDataAccess.listAuths();
        assertEquals(2, authList.size());
    }

    @Test
    public void clearAuthsSuccess() throws DataAccessException {
        String token = "testToken";
        authDataAccess.insertAuth(new AuthData(token, "testUser"));
        authDataAccess.clearAuths();
        assertTrue(authDataAccess.listAuths().isEmpty());
    }

    @Test
    public void getAuthByUsernameSuccess() throws DataAccessException {
        String username = "uniqueUser";
        String token = authDataAccess.generateAuthToken();
        authDataAccess.insertAuth(new AuthData(token, username));
        AuthData retrievedAuth = authDataAccess.getAuthByUsername(username);
        assertNotNull(retrievedAuth);
        assertEquals(username, retrievedAuth.username());
    }

    @Test
    public void getAuthByUsernameFailure() throws DataAccessException {
        assertNull(authDataAccess.getAuthByUsername("nonexistentUser"));
    }
}
