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
    void registerFailure() {
        Exception exception = assertThrows(Exception.class, () -> {
            facade.register("", "", "");
        });
        String expectedMessage = "Registration failed";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage), "Error message should contain '" + expectedMessage + "'.");
    }


}
