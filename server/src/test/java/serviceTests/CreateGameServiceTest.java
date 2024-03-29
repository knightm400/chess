package serviceTests;

import dataAccess.DataAccessException;
import dataAccess.MemoryAuthDataAccess;
import dataAccess.MemoryGameDataAccess;
import model.AuthData;
import model.GameData;
import service.Request.CreateGameRequest;
import service.Result.CreateGameResult;
import service.CreateGameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        memoryAuthDataAccess.insertAuth(new AuthData(authToken, "testUser"));

        CreateGameRequest request = new CreateGameRequest(authToken, "Test Game");
        CreateGameResult result = createGameService.createGame(request);

        assertNotNull(result.gameID(), "Game ID should not be null after game creation.");
        GameData newGameData = memoryGameDataAccess.getGame(result.gameID());
        assertNotNull(newGameData, "New game data should exist in database after creation.");
        assertEquals("Test Game", newGameData.gameName(), "The game name should match the requested name.");
    }

    @Test
    public void createGameFailInvalidToken() {
        String invalidAuthToken = "invalidAuthToken";
        CreateGameRequest request = new CreateGameRequest(invalidAuthToken, "Test Game");

        Exception exception = assertThrows(DataAccessException.class, () -> createGameService.createGame(request));
        assertEquals("Unauthorized", exception.getMessage(), "Should throw DataAccessException due to invalid token.");
    }

    @Test
    public void createGameFailNullGameName() throws Exception {
        String authToken = "testAuthToken";
        memoryAuthDataAccess.insertAuth(new AuthData(authToken, "testUser"));

        CreateGameRequest request = new CreateGameRequest(authToken, null);
        Exception exception = assertThrows(DataAccessException.class, () -> createGameService.createGame(request));
        assertEquals("Bad Request", exception.getMessage(), "Should throw DataAccessException due to null game name.");
    }
}
