package serviceTests;

import dataAccess.DataAccessException;
import dataAccess.MemoryAuthDataAccess;
import dataAccess.MemoryUserDataAccess;
import model.UserData;
import service.Request.LoginRequest;
import service.Result.LoginResult;
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

        userDataAccess.insertUser(new UserData("testUser", "testPassword", "testEmail"));
    }

    @Test
    public void loginSuccess() {
        LoginRequest request = new LoginRequest("testUser", "testPassword");
        LoginResult result = loginService.login(request);

        assertNotNull(result, "Login result should not be null.");
        assertNotNull(result.authToken(), "Auth token should not be null after successful login.");
        assertEquals("testUser", result.username(), "Username should match the one used for login.");
    }

    @Test
    public void loginFailureInvalidCredentials() {
        LoginRequest request = new LoginRequest("testUser", "wrongPassword");
        LoginResult result = loginService.login(request);

        assertNull(result, "Login result should be null for incorrect credentials.");
    }

    @Test
    public void loginFailureNonexistentUser() {
        LoginRequest request = new LoginRequest("nonexistentUser", "testPassword");
        LoginResult result = loginService.login(request);

        assertNull(result, "Login result should be null for nonexistent user.");
    }
}
