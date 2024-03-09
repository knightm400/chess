package serviceTests;

import dataAccess.MemoryAuthDataAccess;
import dataAccess.MemoryGameDataAccess;
import dataAccess.DataAccessException;
import model.AuthData;
import model.GameData;
import service.JoinGameRequest;
import service.JoinGameResult;
import service.JoinGameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

public class JoinGameServiceTest {

    private JoinGameService joinGameService;
    private MemoryAuthDataAccess memoryAuthDataAccess;
    private MemoryGameDataAccess memoryGameDataAccess;

    @BeforeEach
    public void setUp() {
        memoryAuthDataAccess = new MemoryAuthDataAccess();
        memoryGameDataAccess = new MemoryGameDataAccess();
        joinGameService = new JoinGameService(memoryGameDataAccess, memoryAuthDataAccess);
    }

    @Test
    public void joinGameSuccessfully() throws DataAccessException {
        String testAuthToken = memoryAuthDataAccess.generateAuthToken();
        memoryAuthDataAccess.insertAuth(new AuthData(testAuthToken, "testUser"));
        GameData game = new GameData(1234, "testUser", "", "Test Game", "", "WHITE", "");
        memoryGameDataAccess.insertGame(game);

        JoinGameRequest request = new JoinGameRequest(testAuthToken, 1234, "BLACK");
        JoinGameResult result = joinGameService.joinGame(request.authToken(), request.gameID(), request.playerColor());

        assertTrue(result.success(), "Joining game should be successful.");
        assertEquals("BLACK", result.playerColor(), "Player should join as BLACK.");
        assertEquals("testGameID", result.gameID(), "Game ID should match.");
    }

    @Test
    public void joinGameWithInvalidToken() throws DataAccessException {
        JoinGameRequest request = new JoinGameRequest("invalidToken", 1234, "WHITE");
        assertThrows(DataAccessException.class, () -> joinGameService.joinGame(request.authToken(), request.gameID(), request.playerColor()), "Should throw error for invalid token.");
    }

    @Test
    public void joinNonexistentGame() throws DataAccessException {
        String testAuthToken = memoryAuthDataAccess.generateAuthToken();
        memoryAuthDataAccess.insertAuth(new AuthData(testAuthToken, "testUser"));

        JoinGameRequest request = new JoinGameRequest(testAuthToken, 1234, "WHITE");
        assertThrows(DataAccessException.class, () -> joinGameService.joinGame(request.authToken(), request.gameID(), request.playerColor()), "Should throw error for non-existent game.");
    }

}
