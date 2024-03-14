package dataAccessTests;

import dataAccess.DataAccessException;
import dataAccess.DatabaseManager;
import dataAccess.MySqlDataAccess;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MySqlDataAccessTest {

    private static MySqlDataAccess mySqlDataAccess;
    private static final String TEST_USERNAME = "testUser";
    private static final String TEST_PASSWORD = "testPassword";
    private static final String TEST_EMAIL = "testUser@test.com";
    private static final String TEST_AUTH_TOKEN = "testAuthToken";
    private static final int TEST_GAME_ID = 1;
    private static final String TEST_GAME_NAME = "Test Game";
    private static final String TEST_WHITE_COLOR = "WHITE";
    private static final String TEST_BLACK_COLOR = "BLACK";
    private static final String TEST_GAME_DATA = "{}";

    @BeforeAll
    static void setup() {
        mySqlDataAccess = new MySqlDataAccess();
        try {
            DatabaseManager.initializeDatabaseAndTables();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @BeforeEach
    void clearDatabase() throws SQLException {
        try (Connection conn = DatabaseManager.getConnection()) {
            conn.createStatement().executeUpdate("TRUNCATE TABLE Users");
            conn.createStatement().executeUpdate("TRUNCATE TABLE AuthTokens");
            conn.createStatement().executeUpdate("TRUNCATE TABLE Games");
        }
    }

    @Test
    void insertAndGetUserSuccess() throws DataAccessException {
        UserData user = new UserData(TEST_USERNAME, TEST_PASSWORD, TEST_EMAIL);
        mySqlDataAccess.insertUser(user);
        UserData retrievedUser = mySqlDataAccess.getUser(TEST_USERNAME);

        assertNotNull(retrievedUser);
        assertEquals(TEST_USERNAME, retrievedUser.username());
        assertTrue(new BCryptPasswordEncoder().matches(TEST_PASSWORD, retrievedUser.password()));
        assertEquals(TEST_EMAIL, retrievedUser.email());
    }

    @Test
    void getUserThatDoesNotExist() throws DataAccessException {
        UserData retrievedUser = mySqlDataAccess.getUser(TEST_USERNAME);
        assertNull(retrievedUser);
    }
    @Test
    void insertAndDeleteAuthSuccess() throws DataAccessException {
        AuthData auth = new AuthData(TEST_AUTH_TOKEN, TEST_USERNAME);
        mySqlDataAccess.insertAuth(auth);
        AuthData retrievedAuth = mySqlDataAccess.getAuth(TEST_AUTH_TOKEN);

        assertNotNull(retrievedAuth);
        assertEquals(TEST_AUTH_TOKEN, retrievedAuth.authToken());
        assertEquals(TEST_USERNAME, retrievedAuth.username());

        mySqlDataAccess.deleteAuth(TEST_AUTH_TOKEN);
        AuthData deletedAuth = mySqlDataAccess.getAuth(TEST_AUTH_TOKEN);
        assertNull(deletedAuth);
    }

    @Test
    void deleteAuthThatDoesNotExist() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> mySqlDataAccess.deleteAuth(TEST_AUTH_TOKEN));
    }

    @Test
    void insertAndGetGameSuccess() throws DataAccessException {
        GameData game = new GameData(TEST_GAME_ID, TEST_USERNAME, TEST_USERNAME, TEST_GAME_NAME, TEST_GAME_DATA, TEST_WHITE_COLOR, TEST_BLACK_COLOR);
        mySqlDataAccess.insertGame(game);
        GameData retrievedGame = mySqlDataAccess.getGame(TEST_GAME_ID);

        assertNotNull(retrievedGame);
        assertEquals(TEST_GAME_ID, retrievedGame.gameID());
        assertEquals(TEST_USERNAME, retrievedGame.whiteUsername());
        assertEquals(TEST_USERNAME, retrievedGame.blackUsername());
        assertEquals(TEST_GAME_NAME, retrievedGame.gameName());
        assertEquals(TEST_GAME_DATA, retrievedGame.gameData());
        assertEquals(TEST_WHITE_COLOR, retrievedGame.whiteColor());
        assertEquals(TEST_BLACK_COLOR, retrievedGame.blackColor());
    }

    @Test
    void getGameThatDoesNotExist() throws DataAccessException {
        GameData retrievedGame = mySqlDataAccess.getGame(TEST_GAME_ID);
        assertNull(retrievedGame);
    }

    @Test
    void listUsersSuccessful() throws DataAccessException {
        UserData user1 = new UserData(TEST_USERNAME, TEST_PASSWORD, TEST_EMAIL);
        mySqlDataAccess.insertUser(user1);

        List<UserData> users = mySqlDataAccess.listUsers();
        assertFalse(users.isEmpty());
        assertTrue(users.stream().anyMatch(u -> TEST_USERNAME.equals(u.username()) && TEST_EMAIL.equals(u.email())));
    }

    @Test
    void listAuthsSuccessful() throws DataAccessException {
        AuthData auth1 = new AuthData(TEST_AUTH_TOKEN, TEST_USERNAME);
        mySqlDataAccess.insertAuth(auth1);

        List<AuthData> auths = mySqlDataAccess.listAuths();
        assertFalse(auths.isEmpty());
        assertTrue(auths.stream().anyMatch(a -> TEST_AUTH_TOKEN.equals(a.authToken()) && TEST_USERNAME.equals(a.username())));
    }

    @Test
    void listGamesSuccessful() throws DataAccessException {
        GameData game1 = new GameData(TEST_GAME_ID, TEST_USERNAME, TEST_USERNAME, TEST_GAME_NAME, TEST_GAME_DATA, TEST_WHITE_COLOR, TEST_BLACK_COLOR);
        mySqlDataAccess.insertGame(game1);

        List<GameData> games = mySqlDataAccess.listGames();
        assertFalse(games.isEmpty());
        assertTrue(games.stream().anyMatch(g -> TEST_GAME_NAME.equals(g.gameName()) && TEST_GAME_DATA.equals(g.gameData())));
    }

    @Test
    void listGamesEmpty() throws DataAccessException {
        List<GameData> games = mySqlDataAccess.listGames();
        assertTrue(games.isEmpty());
    }

    @Test
    void updateGameSuccess() throws DataAccessException {
        GameData game = new GameData(TEST_GAME_ID, TEST_USERNAME, TEST_USERNAME, TEST_GAME_NAME, TEST_GAME_DATA, TEST_WHITE_COLOR, TEST_BLACK_COLOR);
        mySqlDataAccess.insertGame(game);

        String updatedGameData = "{\"moves\":\"e2e4\"}";
        game = new GameData(TEST_GAME_ID, TEST_USERNAME, TEST_USERNAME, "Updated Test Game", updatedGameData, "BLACK", "WHITE");
        mySqlDataAccess.updateGame(game);

        GameData updatedGame = mySqlDataAccess.getGame(TEST_GAME_ID);
        assertNotNull(updatedGame);
        assertEquals("Updated Test Game", updatedGame.gameName());
        assertEquals(updatedGameData, updatedGame.gameData());
        assertEquals("BLACK", updatedGame.whiteColor());
        assertEquals("WHITE", updatedGame.blackColor());
    }

    @Test
    void updateGameThatDoesNotExist() {
        GameData game = new GameData(999, TEST_USERNAME, TEST_USERNAME, "Non-Existent Game", TEST_GAME_DATA, TEST_WHITE_COLOR, TEST_BLACK_COLOR);
        assertThrows(DataAccessException.class, () -> mySqlDataAccess.updateGame(game));
    }

    @Test
    void clearGamesSuccessful() throws DataAccessException {
        GameData game = new GameData(TEST_GAME_ID, TEST_USERNAME, TEST_USERNAME, TEST_GAME_NAME, TEST_GAME_DATA, TEST_WHITE_COLOR, TEST_BLACK_COLOR);
        mySqlDataAccess.insertGame(game);
        mySqlDataAccess.clearGames();
        List<GameData> games = mySqlDataAccess.listGames();
        assertTrue(games.isEmpty());
    }



}
