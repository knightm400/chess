package serviceTests;

import dataAccess.UserDataAccess;
import model.UserData;
import service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

class UserServiceTest {
    private UserService service;
    private UserDataAccess userDataAccess;

    @BeforeEach
    void setUp() {
        userDataAccess = new UserDataAccess();
        service = new UserService(userDataAccess);
    }

    @Test
    void addUser() throws Exception {
        UserData newUser = new UserData("testUser", "password123", "testUser@example.com");
        service.addUser(newUser);
        UserData fetchedUser = service.getUser("testUser");
        assertEquals(newUser.getUsername(), fetchedUser.getUsername());
    }

    @Test
    void deleteUser() throws Exception {
        UserData newUser = new UserData("testUser", "password123", "testUser@example.com");
        service.addUser(newUser);
        service.deleteUser("testUser");
        assertNull(service.getUser("testUser"));
    }

    @Test
    void listUsers() throws Exception {
        service.clearAllUsers(); // Clear all before testing
        UserData user1 = new UserData("user1", "pass1", "user1@example.com");
        UserData user2 = new UserData("user2", "pass2", "user2@example.com");
        service.addUser(user1);
        service.addUser(user2);
        List<UserData> users = service.listUsers();
        assertTrue(users.contains(user1) && users.contains(user2));
    }

    @Test
    void clearAllUsers() throws Exception {
        UserData newUser = new UserData("testUser", "password123", "testUser@example.com");
        service.addUser(newUser);
        service.clearAllUsers();
        assertTrue(service.listUsers().isEmpty());
    }
}
