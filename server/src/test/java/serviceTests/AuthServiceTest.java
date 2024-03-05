package serviceTests;

import dataAccess.UserDataAccess;
import model.UserData;
import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {
    private AuthService authService;
    private UserDataAccess userDataAccess;

    @BeforeEach
    void setUp() {
        userDataAccess = new UserDataAccess();
        authService = new AuthService(userDataAccess);
    }

    @Test
    void registerSuccessful() throws Exception {
        UserData user = new UserData("testUser", "testPass", "testEmail");
        AuthData authData = authService.register(user);
        assertNotNull(authData);
        assertEquals(user.getUsername(), authData.getUsername());
    }

    @Test
    void registerFailUserExists() throws Exception {
        UserData user = new UserData("existingUser", "testPass", "testEmail");
        authService.register(user);
        assertThrows(Exception.class, () -> authService.register(user));
    }

    @Test
    void loginSuccessful() throws Exception {
        UserData user = new UserData("loginUser", "loginPass", "loginEmail");
        authService.register(user);
        AuthData authData = authService.login(user);
        assertNotNull(authData);
        assertEquals(user.getUsername(), authData.getUsername());
    }

    @Test
    void loginFailWrongCredentials() throws Exception {
        UserData user = new UserData("userWrong", "passWrong", "emailWrong");
        authService.register(user);
        assertThrows(Exception.class, () -> authService.login(new UserData("userWrong", "incorrectPass", "")));
    }

    @Test
    void logout() throws Exception {
        UserData user = new UserData("logoutUser", "logoutPass", "logoutEmail");
        AuthData authData = authService.register(user);
        authService.logout(authData.getAuthToken());
        assertThrows(Exception.class, () -> authService.validateAuth(authData.getAuthToken()));
    }
}
