package serviceTests;

import dataAccess.DataAccessException;
import dataAccess.MemoryUserDataAccess;
import dataAccess.MemoryAuthDataAccess;
import model.UserData;
import service.RegisterRequest;
import service.RegisterResult;
import service.RegisterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RegisterServiceTest {

    private RegisterService registerService;
    private MemoryUserDataAccess memoryUserDataAccess;
    private MemoryAuthDataAccess memoryAuthDataAccess;

    @BeforeEach
    public void setUp() {
        memoryUserDataAccess = new MemoryUserDataAccess();
        memoryAuthDataAccess = new MemoryAuthDataAccess();
        registerService = new RegisterService(memoryUserDataAccess, memoryAuthDataAccess);
    }

    @Test
    public void registerSuccess() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("newUser", "password", "email@example.com");
        RegisterResult result = registerService.register(request);

        assertNotNull(result.authToken(), "Auth token should not be null.");
        assertEquals("newUser", result.username(), "Username should match.");
    }

    @Test
    public void registerFailureAlreadyExists() throws DataAccessException{
        memoryUserDataAccess.insertUser(new UserData("existingUser", "password", "email@example.com"));
        RegisterRequest request = new RegisterRequest("existingUser", "password", "email@example.com");

        assertThrows(DataAccessException.class, () -> registerService.register(request), "Should throw an exception if username already exists.");
    }
}
