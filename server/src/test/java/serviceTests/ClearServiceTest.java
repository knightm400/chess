package serviceTests;

import dataAccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import service.ClearResult;
import service.ClearService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

public class ClearServiceTest {

    private ClearService clearService;
    private MemoryAuthDataAccess memoryAuthDataAccess;
    private MemoryGameDataAccess memoryGameDataAccess;
    private MemoryUserDataAccess memoryUserDataAccess;

    @BeforeEach
    public void setUp() {
        memoryAuthDataAccess = new MemoryAuthDataAccess();
        memoryGameDataAccess = new MemoryGameDataAccess();
        memoryUserDataAccess = new MemoryUserDataAccess();
        clearService = new ClearService(memoryUserDataAccess, memoryGameDataAccess, memoryAuthDataAccess);
    }

    @Test
    public void clearAllDataSuccessfully() throws DataAccessException {
        memoryAuthDataAccess.insertAuth(new AuthData("testToken", "testUser"));
        memoryUserDataAccess.insertUser(new UserData("testUser", "testPass", "testEmail"));
        memoryGameDataAccess.insertGame(new GameData(1234, "testUser1", "testUser2", "testGame", "testData", "WHITE", "BLACK", new HashSet<>()));

        // Action: Clear all data
        ClearResult clearResult = clearService.clearAll();

        // Verify: Check if all data is cleared
        assertEquals("All data cleared successfully.", clearResult.message(), "Message should confirm success.");
        assertTrue(memoryAuthDataAccess.listAuths().isEmpty(), "Auth data should be cleared.");
        assertTrue(memoryUserDataAccess.listUsers().isEmpty(), "User data should be cleared.");
        assertTrue(memoryGameDataAccess.listGames().isEmpty(), "Game data should be cleared.");
    }
}
