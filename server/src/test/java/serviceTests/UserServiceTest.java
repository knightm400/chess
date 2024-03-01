package serviceTests;

import dataAccess.MemoryUserDataAccess;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    private MemoryUserDataAccess dataAccess;
    private UserService service;

    @BeforeEach
    void setUp() {
        dataAccess = new MemoryUserDataAccess();
        service = new UserService(dataAccess);
        dataAccess.clearDatabase(); // Make sure to clear the database before each test
    }

    @Test
    void addUser() {
        UserData user = new UserData("username1", "password", "email@example.com");
        service.addUser(user);

        UserData retrievedUser = service.getUser(user.getUsername());
        assertEquals(user, retrievedUser);
    }

    @Test
    void listUsers() {
        UserData user1 = new UserData("username1", "password", "email@example.com");
        UserData user2 = new UserData("username2", "password", "email@example.com");
        service.addUser(user1);
        service.addUser(user2);

        List<UserData> users = service.listUsers();
        assertEquals(2, users.size());
        assertTrue(users.contains(user1) && users.contains(user2));
    }

    @Test
    void deleteUser() {
        UserData user = new UserData("username1", "password", "email@example.com");
        service.addUser(user);
        service.deleteUser(user.getUsername());

        assertNull(service.getUser(user.getUsername()));
    }

    @Test
    void clearAllUsers() {
        service.addUser(new UserData("username1", "password", "email@example.com"));
        service.addUser(new UserData("username2", "password", "email@example.com"));

        service.clearAllUsers();
        assertTrue(service.listUsers().isEmpty());
    }
}
