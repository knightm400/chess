package dataAccessTests;

import dataAccess.DataAccessException;
import dataAccess.MemoryGameDataAccess;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GameDataAccessTest {

    private MemoryGameDataAccess gameDataAccess;
    private GameData sampleGame;

    @BeforeEach
    public void setUp() {
        gameDataAccess = new MemoryGameDataAccess();
        sampleGame = new GameData(1, "user1", "user2", "Game1", "data", "WHITE", "BLACK");
    }

    @Test
    public void insertGameSuccessfully() throws DataAccessException {
        gameDataAccess.insertGame(sampleGame);
        assertNotNull(gameDataAccess.getGame(sampleGame.gameID()));
    }

    @Test
    public void insertGameFailure() {
        assertThrows(DataAccessException.class, () -> {
            gameDataAccess.insertGame(sampleGame);
            gameDataAccess.insertGame(sampleGame);
        });
    }

    @Test
    public void getGameSuccessfully() throws DataAccessException {
        gameDataAccess.insertGame(sampleGame);
        assertEquals(sampleGame, gameDataAccess.getGame(sampleGame.gameID()));
    }

    @Test
    public void getGameFailure() throws DataAccessException {
        assertNull(gameDataAccess.getGame(999));
    }

    @Test
    public void updateGameSuccessfully() throws DataAccessException {
        gameDataAccess.insertGame(sampleGame);
        GameData updatedGame = new GameData(sampleGame.gameID(), "newUser1", "newUser2", "NewGame", "newData", "BLACK", "WHITE");
        gameDataAccess.updateGame(updatedGame);
        assertEquals(updatedGame, gameDataAccess.getGame(updatedGame.gameID()));
    }

    @Test
    public void updateGameFailure() {
        assertThrows(DataAccessException.class, () -> {
            GameData nonExistingGame = new GameData(999, "user3", "user4", "Game999", "data999", "BLACK", "WHITE");
            gameDataAccess.updateGame(nonExistingGame);
        });
    }

    @Test
    public void listGamesSuccessfully() throws DataAccessException {
        gameDataAccess.insertGame(sampleGame);
        List<GameData> games = gameDataAccess.listGames();
        assertTrue(games.contains(sampleGame));
        assertEquals(1, games.size());
    }

    @Test
    public void clearGamesSuccessfully() throws DataAccessException {
        gameDataAccess.insertGame(sampleGame);
        gameDataAccess.clearGames();
        assertTrue(gameDataAccess.listGames().isEmpty());
    }
}
