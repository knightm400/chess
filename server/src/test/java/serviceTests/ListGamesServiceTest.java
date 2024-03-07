package serviceTests;

import dataAccess.DataAccessException;
import dataAccess.MemoryGameDataAccess;
import model.GameData;
import service.ListGamesRequest;
import service.ListGamesResult;
import service.ListGamesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

public class ListGamesServiceTest {

    private ListGamesService listGamesService;
    private MemoryGameDataAccess memoryGameDataAccess;

    @BeforeEach
    public void setUp() throws DataAccessException {
        memoryGameDataAccess = new MemoryGameDataAccess();
        listGamesService = new ListGamesService(memoryGameDataAccess);

        // Set up some dummy games for testing
        memoryGameDataAccess.insertGame(new GameData("game1", "user1", "user2", "Game 1", "", "WHITE", "", new HashSet<>()));
        memoryGameDataAccess.insertGame(new GameData("game2", "user3", "user4", "Game 2", "", "BLACK", "", new HashSet<>()));
    }

    @Test
    public void listAllGamesSuccessfully() throws DataAccessException {
        ListGamesRequest request = new ListGamesRequest(); // Assuming your request doesn't require specific parameters
        ListGamesResult result = listGamesService.listGames(request);

        // Check the result is successful and contains the expected games
        assertNotNull(result.getGames(), "Game list should not be null.");
        assertEquals(2, result.getGames().size(), "There should be two games listed.");

        // More detailed checks could include verifying the content of each GameData object
    }

    // More tests can be added for different scenarios, such as an empty list of games, or error handling paths.
}
