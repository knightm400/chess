package serviceTests;

import dataAccess.UserDataAccess;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.UserService;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {
    private UserDataAccess userDataAccess;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userDataAccess = new UserDataAccess();
        userService = new UserService(userDataAccess);
    }

    @Test
    void createUser_success() throws Exception {
        UserData newUser = userService.createUser("testUser", "password", "email@example.com");
        assertNotNull(newUser);
        assertEquals("testUser", newUser.getUsername());
    }

    @Test
    void getUser_success() throws Exception {
        UserData expectedUser = new UserData("testUser", "password", "email@example.com");
        userDataAccess.insertUser(expectedUser);
        UserData retrievedUser = userService.getUser("testUser");
        assertNotNull(retrievedUser);
        assertEquals(expectedUser.getUsername(), retrievedUser.getUsername());
    }
}
