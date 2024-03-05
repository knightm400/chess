package serviceTests;

import dataAccess.GameDataAccess;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

class GameServiceTest {
    private GameService service;
    private GameDataAccess gameDataAccess;

    @BeforeEach
    void setUp() {
        gameDataAccess = new GameDataAccess();
        service = new GameService(gameDataAccess);
    }

    @Test
    void createGame() throws Exception {
        GameData game = new GameData("gameID", "whiteUsername", "blackUsername", "gameName");
        service.createGame(game);
        assertNotNull(service.getGame("gameID"));
    }

    @Test
    void getGame() throws Exception {
        GameData game = new GameData("gameID", "whiteUsername", "blackUsername", "gameName");
        gameDataAccess.insertGame(game); // Directly insert to simulate existing game
        assertNotNull(service.getGame("gameID"));
    }

    @Test
    void updateGame() throws Exception {
        GameData game = new GameData("gameID", "whiteUsername", "blackUsername", "gameName");
        gameDataAccess.insertGame(game);
        game.setGameName("newGameName");
        service.updateGame("gameID", game);
        assertEquals("newGameName", service.getGame("gameID").getGameName());
    }

    @Test
    void deleteGame() throws Exception {
        GameData game = new GameData("gameID", "whiteUsername", "blackUsername", "gameName");
        gameDataAccess.insertGame(game);
        service.deleteGame("gameID");
        assertNull(service.getGame("gameID"));
    }

    @Test
    void listAllGames() throws Exception {
        GameData game1 = new GameData("gameID1", "whiteUsername1", "blackUsername1", "gameName1");
        GameData game2 = new GameData("gameID2", "whiteUsername2", "blackUsername2", "gameName2");
        gameDataAccess.insertGame(game1);
        gameDataAccess.insertGame(game2);
        List<GameData> games = service.listAllGames();
        assertTrue(games.contains(game1) && games.contains(game2));
    }
}
