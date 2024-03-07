package serviceTests;

import dataAccess.*;
import model.AuthData;
import model.UserData;
import service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    private UserService userService;
    private MemoryUserDataAccess userDataAccess;
    private MemoryAuthDataAccess authDataAccess;

    @BeforeEach
    public void setUp() {
        userDataAccess = new MemoryUserDataAccess();
        authDataAccess = new MemoryAuthDataAccess();
        userService = new UserService(userDataAccess, authDataAccess);
    }

    @Test
    public void registerSuccess() throws DataAccessException {
        UserData newUser = new UserData("newUser", "password", "email@example.com");
        AuthData registeredUser = userService.register(newUser);
        assertNotNull(registeredUser.authToken());
        assertEquals("newUser", registeredUser.username());
    }

    @Test
    public void loginSuccess() throws DataAccessException {
        userDataAccess.insertUser(new UserData("existingUser", "password", "email@example.com"));
        AuthData loggedInUser = userService.login("existingUser", "password");
        assertNotNull(loggedInUser.authToken());
        assertEquals("existingUser", loggedInUser.username());
    }

    @Test
    public void logoutSuccess() throws DataAccessException {
        userDataAccess.insertUser(new UserData("user", "password", "email@example.com"));
        AuthData loggedInUser = userService.login("user", "password");
        assertDoesNotThrow(() -> userService.logout(loggedInUser.authToken()));
    }

}
