package serviceTests;

import dataAccess.AuthDataAccess;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.AuthService;

import static org.junit.jupiter.api.Assertions.*;
public class AuthServiceTest {
    private AuthService authService;
    private IAuthDataAccess authDataAccess;

    @BeforeEach
    void setUp() {
        authDataAccess = new AuthDataAccess();
        authService = new AuthService(authDataAccess);
    }

    @Test
    void createAuth_success() throws Exception {
        String username = "testUser";
        String authToken = authService.createAuth(username);
        assertNotNull(authToken);
        assertEquals(username, authService.getAuth(authToken));
    }

    @Test
    void getAuth_success() throws Exception {
        String username = "testUser";
        String authToken = authService.createAuth(username);
        String retrievedUsername = authService.getAuth(authToken);
        assertEquals(username, retrievedUsername);
    }

    @Test
    void deleteAuth_success() throws Exception {
        String username = "testUser";
        String authToken = authService.createAuth(username);
        authService.deleteAuth(authToken);
        assertNull(authService.getAuth(authToken));
    }
}
