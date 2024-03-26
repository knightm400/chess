package clientTests;

import org.junit.jupiter.api.*;
import server.Server;
import ui.ServerFacade;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade();
        facade.setServerBaseUrl("http://localhost:" + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    void clearDatabase() throws Exception {
        facade.clearData();
    }

    @Test
    void registerSuccess() throws Exception {
        var authData = facade.register("player1", "password", "p1@email.com");
        assertNotNull(authData, "Auth token should not be null after successful registration.");
        assertTrue(authData.length() > 10, "Auth token length should be more than 10 characters.");
    }

    @Test
    void registerFailureWhenUsernameIsEmpty() {
        Exception exception = assertThrows(Exception.class, () -> {
            facade.register("", "password123", "email@example.com");
        });
        String expectedMessage = "Error: Username, password, and email cannot be empty.";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage), "Error message should contain '" + expectedMessage + "' when username is empty.");
    }

    @Test
    void registerFailureWhenPasswordIsEmpty() {
        Exception exception = assertThrows(Exception.class, () -> {
            facade.register("username", "", "email@example.com");
        });
        String expectedMessage = "Error: Username, password, and email cannot be empty.";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage), "Error message should contain '" + expectedMessage + "' when password is empty.");
    }

    @Test
    void registerFailureWhenEmailIsEmpty() {
        Exception exception = assertThrows(Exception.class, () -> {
            facade.register("username", "password123", "");
        });
        String expectedMessage = "Error: Username, password, and email cannot be empty.";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage), "Error message should contain '" + expectedMessage + "' when email is empty.");
    }

    @Test
    void loginSuccess() throws Exception {
        facade.register("player2", "password123", "p2@email.com");
        String authToken = facade.login("player2", "password123");
        assertNotNull(authToken, "Auth token should not be null after successful login.");
        assertTrue(authToken.length() > 10, "Auth token length should be more than 10 characters.");
    }

    @Test
    void loginFailure() {
        Exception exception = assertThrows(Exception.class, () -> {
            facade.login("nonexistentuser", "wrongpassword");
        });
        String expectedMessage = "Server returned HTTP response code: 401";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage), "Error message should contain '" + expectedMessage + "'.");
    }

    @Test
    void logoutSuccess() throws Exception {
        facade.register("player3", "password456", "p3@email.com");
        String authToken = facade.login("player3", "password456");
        assertNotNull(authToken, "Auth token should not be null after successful login for logout test.");

        String logoutResponse = facade.logout();
        assertEquals("Logged out successfully.", logoutResponse, "Logout response should be 'Logged out successfully.'.");
    }

    @Test
    void createGameSuccess() throws Exception {
        facade.register("gameCreator", "createPass", "create@example.com");
        facade.login("gameCreator", "createPass");

        var gameData = facade.createGame(facade.getAuthToken(), "ChessMatch");
        assertNotNull(gameData, "GameData should not be null after successful game creation.");
        assertNotNull(gameData.gameID(), "Game ID should not be null after successful game creation.");
    }

    @Test
    void createGameFailure() {
        Exception exception = assertThrows(Exception.class, () -> {
            facade.createGame("", "");
        });
        String expectedMessage = "Failed to create game";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage), "Error message should contain '" + expectedMessage + "'.");
    }

    @Test
    void listGamesWhenEmpty() throws Exception {
        facade.clearData();
        facade.setAuthToken(null);
        facade.register("testUser", "password", "test@example.com");
        String authToken = facade.login("testUser", "password");
        facade.setAuthToken(authToken);
        var games = facade.listGames();
        assertTrue(games.isEmpty(), "Games list should be empty when no games have been created.");
    }

    @Test
    void listGamesAfterCreation() throws Exception {
        facade.register("listGamesUser", "listPass", "list@example.com");
        facade.login("listGamesUser", "listPass");

        facade.createGame(facade.getAuthToken(), "NewChessMatch");

        var games = facade.listGames();
        assertFalse(games.isEmpty(), "Games list should not be empty after a game has been created.");
        assertEquals(1, games.size(), "There should be exactly one game listed after creation.");
        assertEquals("NewChessMatch", games.get(0).gameName(), "The created game should be 'NewChessMatch'.");
    }

    @Test
    void joinGameSuccess() throws Exception {
        facade.register("playerForJoin", "joinPass", "join@example.com");
        facade.login("playerForJoin", "joinPass");
        var gameData = facade.createGame(facade.getAuthToken(), "JoinableChessMatch");

        var joinResult = facade.joinGame(facade.getAuthToken(), gameData.gameID(), "black");
        assertNotNull(joinResult, "JoinGameResult should not be null after successfully joining a game.");
        assertEquals(gameData.gameID(), joinResult.gameID(), "Joined game ID should match the created game ID.");
    }

    @Test
    void joinGameFailure() throws Exception {
        facade.register("playerForJoinFail", "joinFailPass", "joinfail@example.com");
        facade.login("playerForJoinFail", "joinFailPass");

        Exception exception = assertThrows(Exception.class, () -> {
            facade.joinGame(facade.getAuthToken(), 99999, "white");
        });
        String expectedMessage = "Failed to join game";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage), "Error message should contain '" + expectedMessage + "'.");
    }

    @Test
    void joinGameAsObserverSuccess() throws Exception {
        facade.register("observer", "observerPass", "observer@example.com");
        facade.login("observer", "observerPass");
        var gameData = facade.createGame(facade.getAuthToken(), "ObservableChessMatch");

        boolean observerJoinSuccess = facade.joinGameAsObserver(gameData.gameID());
        assertTrue(observerJoinSuccess, "Should be able to join the game as an observer successfully.");
    }

    @Test
    void joinGameAsObserverFailure() throws Exception {
        facade.register("observerFail", "observerFailPass", "observerfail@example.com");
        facade.login("observerFail", "observerFailPass");

        Exception exception = assertThrows(Exception.class, () -> {
            facade.joinGameAsObserver(99999);
        });
        String expectedMessage = "Failed to join game as observer";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage), "Error message should contain '" + expectedMessage + "'.");
    }
}

