package serviceTests;

import dataAccess.GameDataAccess;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.GameService;

import java.util.List;

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

    @Test
    void getGame_success() throws Exception {
        GameData game = new GameData("gameID1", "Chess", "GameData");
        gameDataAccess.createGame(game);
        GameData found = gameService.getGame("gameID1");
        assertNotNull(found);
        assertEquals("Chess", found.getGameName());
    }

    @Test
    void listGames_success() throws Exception {
        GameData game1 = new GameData("gameID1", "Chess", "GameData");
        GameData game2 = new GameData("gameID2", "Checkers", "GameData");
        gameDataAccess.createGame(game1);
        gameDataAccess.createGame(game2);
        List<GameData> games = gameService.listGames();
        assertEquals(2, games.size());

    }

    @Test
    void updateGame_success() throws Exception {
        GameData orginalGame = new GameData("gameID", "OldName", "OldData");
        gameDataAccess.createGame(orginalGame);
        GameData updatedGame = new GameData("gameID", "NewName", "NewData");
        GameData result = gameService.getGame("gameID");
        assertEquals("NewName", result.getGameName());
        assertEquals("NewData", result.getGameData());
    }
}
