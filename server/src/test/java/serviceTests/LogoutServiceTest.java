package serviceTests;

import dataAccess.DataAccessException;
import dataAccess.MemoryAuthDataAccess;
import model.AuthData;
import service.LogoutRequest;
import service.LogoutService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LogoutServiceTest {

    private LogoutService logoutService;
    private MemoryAuthDataAccess authDataAccess;

    @BeforeEach
    public void setUp() {
        authDataAccess = new MemoryAuthDataAccess();
        logoutService = new LogoutService(authDataAccess);
    }

    @Test
    public void logoutSuccess() throws DataAccessException {
        String testAuthToken = "validToken";
        authDataAccess.insertAuth(new AuthData(testAuthToken, "testUser"));

        LogoutRequest request = new LogoutRequest(testAuthToken);
        assertDoesNotThrow(() -> logoutService.logout(request), "Logging out should not throw an exception for a valid token.");
    }

    @Test
    public void logoutFailureInvalidToken() throws DataAccessException {
        String invalidToken = "invalidToken";

        LogoutRequest request = new LogoutRequest(invalidToken);
        assertThrows(DataAccessException.class, () -> logoutService.logout(request), "Should throw an exception for an invalid token.");
    }
}
