package serviceTests;

import dataAccess.DataAccessException;
import dataAccess.MemoryAuthDataAccess;
import dataAccess.MemoryGameDataAccess;
import model.AuthData;
import model.GameData;
import service.Request.CreateGameRequest;
import service.Result.CreateGameResult;
import service.GameService;
import service.Request.JoinGameRequest;
import service.Result.JoinGameResult;
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

        assertNotNull(createResult.gameID(), "Game creation should be successful and return a valid game ID.");

        JoinGameRequest joinRequest = new JoinGameRequest(testAuthToken, createResult.gameID(), "BLACK");
        JoinGameResult joinResult = gameService.joinGame(joinRequest);

        assertTrue(joinResult.success(), "Joining game should be successful. Message: " + joinResult.message());

        GameData joinedGame = memoryGameDataAccess.getGame(createResult.gameID());

        assertEquals("testUser", joinedGame.blackUsername(), "Black player should match the joining user.");
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
        JoinGameResult initialJoinResult = gameService.joinGame(initialJoinRequest);
        assertTrue(initialJoinResult.success(), "First joining of the game should be successful.");

        JoinGameRequest secondJoinRequest = new JoinGameRequest(anotherTestAuthToken, createResult.gameID(), "BLACK");
        JoinGameResult secondJoinResult = gameService.joinGame(secondJoinRequest);

        assertFalse(secondJoinResult.success(), "Joining game should fail as color is already taken.");
        assertEquals("Black color already taken.", secondJoinResult.message(), "Error message should indicate that the black color is already taken.");
    }

    @Test
    public void joinGameFailInvalidGame() throws DataAccessException {
        String testAuthToken = memoryAuthDataAccess.generateAuthToken();
        memoryAuthDataAccess.insertAuth(new AuthData(testAuthToken, "testUser"));
        JoinGameRequest joinRequest = new JoinGameRequest(testAuthToken, 99999, "WHITE");

        JoinGameResult result = gameService.joinGame(joinRequest);

        assertFalse(result.success(), "Joining a non-existent game should not be successful.");

        assertEquals("Game does not exist.", result.message(), "Error message should indicate that the game does not exist.");
    }
}
