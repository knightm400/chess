package serviceTests;

import dataAccess.DataAccessException;
import dataAccess.MemoryAuthDataAccess;
import dataAccess.MemoryGameDataAccess;
import model.AuthData;
import model.GameData;
import service.GameService;
import service.Request.CreateGameRequest;
import service.Request.ListGamesRequest;
import service.Result.ListGamesResult;
import service.ListGamesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ListGamesServiceTest {

    private ListGamesService listGamesService;
    private MemoryGameDataAccess memoryGameDataAccess;
    private MemoryAuthDataAccess memoryAuthDataAccess;
    private GameService gameService;

    @BeforeEach
    public void setUp() throws DataAccessException {
        memoryGameDataAccess = new MemoryGameDataAccess();
        memoryAuthDataAccess = new MemoryAuthDataAccess();
        listGamesService = new ListGamesService(memoryGameDataAccess, memoryAuthDataAccess);

        // Inserting mock data into memory data access
        String dummyAuthToken = memoryAuthDataAccess.generateAuthToken();
        memoryAuthDataAccess.insertAuth(new AuthData(dummyAuthToken, "testUser"));
        memoryGameDataAccess.insertGame(new GameData(1, "user1", "user2", "Game 1", "", "WHITE", ""));
        memoryGameDataAccess.insertGame(new GameData(2, "user3", "user4", "Game 2", "", "BLACK", ""));
    }

    @Test
    public void listAllGamesSuccessfully() throws DataAccessException {
        String dummyAuthToken = memoryAuthDataAccess.generateAuthToken();
        memoryAuthDataAccess.insertAuth(new AuthData(dummyAuthToken, "dummyUser"));

        gameService.createGame(new CreateGameRequest(dummyAuthToken, "Game1"));
        gameService.createGame(new CreateGameRequest(dummyAuthToken, "Game2"));

        ListGamesRequest request = new ListGamesRequest();
        ListGamesResult result = listGamesService.listGames(dummyAuthToken, request);

        assertNotNull(result.getGames(), "Game list should not be null.");
        assertEquals(2, result.getGames().size(), "There should be two games listed.");    }

    @Test
    public void listGamesFailWithInvalidToken() {
        ListGamesRequest request = new ListGamesRequest();
        String invalidAuthToken = "invalidAuthToken";

        assertThrows(DataAccessException.class, () -> listGamesService.listGames(invalidAuthToken, request), "Should throw DataAccessException due to unauthorized access.");
    }
}
