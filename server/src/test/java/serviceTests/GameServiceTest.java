package serviceTests;

import dataAccess.DataAccessException;
import dataAccess.MemoryAuthDataAccess;
import dataAccess.MemoryGameDataAccess;
import model.AuthData;
import service.CreateGameRequest;
import service.CreateGameResult;
import service.GameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {

    private GameService gameService;
    private MemoryAuthDataAccess memoryAuthDataAccess;
    private MemoryGameDataAccess memoryGameDataAccess;

    @BeforeEach
    public void setUp() {
        memoryAuthDataAccess = new MemoryAuthDataAccess();
        memoryGameDataAccess = new MemoryGameDataAccess();
        gameService = new GameService(memoryGameDataAccess, memoryAuthDataAccess);
    }

    @Test
    public void createGameSuccessfully() throws DataAccessException {
        String testAuthToken = memoryAuthDataAccess.generateAuthToken();
        memoryAuthDataAccess.insertAuth(new AuthData(testAuthToken, "testUser"));

        CreateGameRequest request = new CreateGameRequest(testAuthToken, "testGame");
        CreateGameResult result = gameService.createGame(request);

        assertNotNull(result.gameID(), "Game creation should be successful and gameID should not be null.");
        assertEquals("testGame", memoryGameDataAccess.getGame(result.gameID()).gameName(), "Game name should match.");
    }
}
