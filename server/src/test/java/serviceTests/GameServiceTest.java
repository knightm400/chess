package serviceTests;

import dataAccess.GameDataAccess;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.GameService;

import static org.junit.jupiter.api.Assertions.*;


public class GameServiceTest {
    private GameDataAccess gameDataAccess;
    private GameService gameService;

    @BeforeEach
    void setUp() {
        gameDataAccess = new GameDataAccess();
        gameService = new GameService(gameDataAccess);
    }

    @Test
    void createGame_success() throws Exception {
        GameData newGame = gameService.createGame("Chess Championship", "testUser");
        assertNotNull(newGame);
        assertEquals("Chess Championship", newGame.getGameName());
    }
}
