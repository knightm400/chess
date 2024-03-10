package serviceTests;

import dataAccess.DataAccessException;
import dataAccess.MemoryAuthDataAccess;
import dataAccess.MemoryGameDataAccess;
import model.AuthData;
import service.CreateGameRequest;
import service.CreateGameResult;
import service.GameService;
import service.JoinGameRequest;
import service.JoinGameResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {

    private GameService gameService;
    private MemoryAuthDataAccess memoryAuthDataAccess;
    private MemoryGameDataAccess memoryGameDataAccess;

    @BeforeEach
    public void setUp() throws DataAccessException {
        memoryAuthDataAccess = new MemoryAuthDataAccess();
        memoryGameDataAccess = new MemoryGameDataAccess();
        gameService = new GameService(memoryGameDataAccess, memoryAuthDataAccess);
        String testAuthToken = memoryAuthDataAccess.generateAuthToken();
        memoryAuthDataAccess.insertAuth(new AuthData(testAuthToken, "testUser"));
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

    @Test
    public void createGameFailInvalidToken() {
        String invalidAuthToken = "invalidToken";
        CreateGameRequest request = new CreateGameRequest(invalidAuthToken, "testGame");

        assertThrows(DataAccessException.class, () -> gameService.createGame(request), "Should throw DataAccessException due to invalid token.");
    }

    @Test
    public void joinGameSuccessfully() throws DataAccessException {
        String testAuthToken = memoryAuthDataAccess.generateAuthToken();
        memoryAuthDataAccess.insertAuth(new AuthData(testAuthToken, "testUser"));
        CreateGameRequest createRequest = new CreateGameRequest(testAuthToken, "testGame");
        CreateGameResult createResult = gameService.createGame(createRequest);

        JoinGameRequest joinRequest = new JoinGameRequest(testAuthToken, createResult.gameID(), "BLACK");
        JoinGameResult joinResult = gameService.joinGame(joinRequest);

        assertTrue(joinResult.success(), "Joining game should be successful.");
        assertEquals("BLACK", memoryGameDataAccess.getGame(joinResult.gameID()).blackUsername(), "Black player should match the joining user.");
    }

    @Test
    public void joinGameFailColorAlreadyTaken() throws DataAccessException {
        String testAuthToken = memoryAuthDataAccess.generateAuthToken();
        memoryAuthDataAccess.insertAuth(new AuthData(testAuthToken, "testUser"));
        String anotherTestAuthToken = memoryAuthDataAccess.generateAuthToken();
        memoryAuthDataAccess.insertAuth(new AuthData(anotherTestAuthToken, "anotherTestUser"));
        CreateGameRequest createRequest = new CreateGameRequest(testAuthToken, "testGame");
        CreateGameResult createResult = gameService.createGame(createRequest);
        JoinGameRequest initialJoinRequest = new JoinGameRequest(testAuthToken, createResult.gameID(), "BLACK");
        gameService.joinGame(initialJoinRequest);

        JoinGameRequest secondJoinRequest = new JoinGameRequest(anotherTestAuthToken, createResult.gameID(), "BLACK");
        assertThrows(DataAccessException.class, () -> gameService.joinGame(secondJoinRequest), "Should throw DataAccessException because color is already taken.");
    }

    @Test
    public void joinGameFailInvalidGame() throws DataAccessException {
        String testAuthToken = memoryAuthDataAccess.generateAuthToken();
        memoryAuthDataAccess.insertAuth(new AuthData(testAuthToken, "testUser"));
        JoinGameRequest joinRequest = new JoinGameRequest(testAuthToken, 99999, "WHITE");
        assertThrows(DataAccessException.class, () -> gameService.joinGame(joinRequest), "Should throw DataAccessException because game does not exist.");
    }
}
