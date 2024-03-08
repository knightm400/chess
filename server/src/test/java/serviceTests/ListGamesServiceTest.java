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

        memoryGameDataAccess.insertGame(new GameData(1, "user1", "user2", "Game 1", "", "WHITE", "", new HashSet<>()));
        memoryGameDataAccess.insertGame(new GameData(2, "user3", "user4", "Game 2", "", "BLACK", "", new HashSet<>()));
    }

    @Test
    public void listAllGamesSuccessfully() throws DataAccessException {
        ListGamesRequest request = new ListGamesRequest();
        ListGamesResult result = listGamesService.listGames(request);

        assertNotNull(result.getGames(), "Game list should not be null.");
        assertEquals(2, result.getGames().size(), "There should be two games listed.");

    }

}
