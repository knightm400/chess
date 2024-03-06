package serviceTests;

import dataAccess.*;
import model.AuthData;
import model.UserData;
import service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AuthServiceTest {

    private AuthService authService;
    private MemoryAuthDataAccess authDA;
    private MemoryUserDataAccess userDA;

    @BeforeEach
    public void setUp() {
        authDA = new MemoryAuthDataAccess();
        userDA = new MemoryUserDataAccess();
        authService = new AuthService(userDA, authDA);
    }

    @AfterEach
    public void tearDown() {
        authDA.clearAuths();
        userDA.clearUsers();
    }

    @Test
    public void testRegisterSuccess() throws DataAccessException {
        UserData newUser = new UserData("testUser", "testPass", "testEmail");
        AuthData registeredUser = authService.register(newUser);
        assertNotNull(registeredUser.authToken());
        assertEquals("testUser", registeredUser.username());
    }

    @Test
    public void testRegisterFailAlreadyExists() {
        UserData newUser = new UserData("testUser", "testPass", "testEmail");
        assertThrows(DataAccessException.class, () -> {
            authService.register(newUser);
            authService.register(newUser);
        });
    }

    @Test
    public void testLogoutSuccess() throws DataAccessException {
        UserData newUser = new UserData("testUser", "testPass", "testEmail");
        AuthData registeredUser = authService.register(newUser);
        assertDoesNotThrow(() -> authService.logout(registeredUser.authToken()));
    }

    @Test
    public void testLogoutFailInvalidToken() {
        assertThrows(DataAccessException.class, () -> authService.logout("invalidToken"));
    }
}
