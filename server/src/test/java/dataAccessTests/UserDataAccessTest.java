package dataAccessTests;

import dataAccess.DataAccessException;
import dataAccess.MemoryUserDataAccess;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserDataAccessTest {
    private MemoryUserDataAccess dao;

    @BeforeEach
    public void setUp() {
        dao = new MemoryUserDataAccess();
    }

    @Test
    public void insertUserSuccessful() throws DataAccessException {
        UserData newUser = new UserData("newUser", "password", "newuser@example.com");
        dao.insertUser(newUser);
        UserData retrievedUser = dao.getUser("newUser");
        assertNotNull(retrievedUser);
        assertEquals("newUser", retrievedUser.username());
    }

    @Test
    public void insertUserFailDuplicate() {
        UserData newUser = new UserData("newUser", "password", "newuser@example.com");
        assertDoesNotThrow(() -> dao.insertUser(newUser));

        UserData duplicateUser = new UserData("newUser", "password123", "newuser@example.com");
        assertThrows(DataAccessException.class, () -> dao.insertUser(duplicateUser));
    }

    @Test
    public void getUserSuccessful() throws DataAccessException {
        UserData newUser = new UserData("testUser", "password", "test@example.com");
        dao.insertUser(newUser);
        UserData result = dao.getUser("testUser");
        assertNotNull(result);
        assertEquals("testUser", result.username());
        assertEquals("password", result.password());
    }

    @Test
    public void getUserFail() throws DataAccessException {
        UserData newUser = new UserData("testUser", "password", "test@example.com");
        dao.insertUser(newUser);
        assertNull(dao.getUser("nonExistentUser"));
    }

    @Test
    public void listUsersSuccessful() throws DataAccessException {
        UserData user1 = new UserData("user1", "password1", "user1@example.com");
        UserData user2 = new UserData("user2", "password2", "user2@example.com");
        dao.insertUser(user1);
        dao.insertUser(user2);

        var users = dao.listUsers();
        assertNotNull(users);
        assertTrue(users.size() >= 2);
        assertTrue(users.contains(user1));
        assertTrue(users.contains(user2));
    }

    @Test
    public void validateUserSuccessful() throws DataAccessException {
        UserData newUser = new UserData("validUser", "rightpassword", "user@example.com");
        dao.insertUser(newUser);

        UserData validatedUser = dao.validateUser("validUser", "rightpassword");
        assertNotNull(validatedUser);
        assertEquals("validUser", validatedUser.username());
    }

    @Test
    public void validateUserFail() {
        UserData newUser = new UserData("validUser", "rightpassword", "user@example.com");
        assertDoesNotThrow(() -> dao.insertUser(newUser));

        assertThrows(DataAccessException.class, () -> dao.validateUser("validUser", "wrongpassword"));
    }

    @Test
    public void clearUsersSuccessful() throws DataAccessException {
        UserData newUser = new UserData("user", "password", "user@example.com");
        dao.insertUser(newUser);

        dao.clearUsers();
        assertTrue(dao.listUsers().isEmpty());
    }
}
