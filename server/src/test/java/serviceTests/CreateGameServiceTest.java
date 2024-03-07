package serviceTests;

import dataAccess.MemoryAuthDataAccess;
import dataAccess.MemoryGameDataAccess;
import model.AuthData;
import model.GameData;
import service.CreateGameRequest;
import service.CreateGameResult;
import service.CreateGameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import static org.junit.jupiter.api.Assertions.*;

public class CreateGameServiceTest {

    private CreateGameService createGameService;
    private MemoryAuthDataAccess memoryAuthDataAccess;
    private MemoryGameDataAccess memoryGameDataAccess;

    @BeforeEach
    public void setUp() {
        memoryAuthDataAccess = new MemoryAuthDataAccess();
        memoryGameDataAccess = new MemoryGameDataAccess();
        createGameService = new CreateGameService(memoryAuthDataAccess, memoryGameDataAccess);
    }

    @Test
    public void createGameSuccessfully() throws Exception {
        String authToken = "testAuthToken";
        memoryAuthDataAccess.insertAuth(new AuthData(authToken, "testUser")); // Pre-insert auth data for validation

        CreateGameRequest request = new CreateGameRequest(authToken, "Test Game");
        CreateGameResult result = createGameService.createGame(request);

        assertNotNull(result.gameID(), "Game ID should not be null after game creation.");
        GameData newGameData = memoryGameDataAccess.getGame(result.gameID());
        assertNotNull(newGameData, "New game data should exist in database after creation.");
        assertEquals("Test Game", newGameData.gameID(), "The game name should match the requested name.");
    }
}
