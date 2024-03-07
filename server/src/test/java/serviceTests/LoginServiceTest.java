package serviceTests;

import dataAccess.DataAccessException;
import dataAccess.MemoryAuthDataAccess;
import dataAccess.MemoryUserDataAccess;
import model.UserData;
import service.LoginRequest;
import service.LoginResult;
import service.LoginService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LoginServiceTest {

    private LoginService loginService;
    private MemoryAuthDataAccess authDataAccess;
    private MemoryUserDataAccess userDataAccess;

    @BeforeEach
    public void setUp() throws DataAccessException {
        authDataAccess = new MemoryAuthDataAccess();
        userDataAccess = new MemoryUserDataAccess();
        loginService = new LoginService(authDataAccess, userDataAccess);

        // Set up a dummy user for testing
        userDataAccess.insertUser(new UserData("testUser", "testPassword", "testEmail"));
    }

    @Test
    public void loginSuccess() throws DataAccessException {
        LoginRequest request = new LoginRequest("testUser", "testPassword");
        LoginResult result = loginService.login(request);

        assertNotNull(result.authToken(), "Auth token should not be null.");
        assertEquals("testUser", result.username(), "Username should match.");
    }

    @Test
    public void loginFailure() {
        LoginRequest request = new LoginRequest("testUser", "wrongPassword");
        assertThrows(DataAccessException.class, () -> loginService.login(request), "Should throw exception for wrong password.");
    }
}
