package server;

import dataAccess.*;
import spark.*;
import org.eclipse.jetty.websocket.api.Session;
import java.sql.SQLException;
import server.WebSocket.WebSocketHandler;

public class Server {
    private final UserDataAccess userDataAccess;
    private final GameDataAccess gameDataAccess;
    private final AuthDataAccess authDataAccess;
    private final WebSocketHandler webSocketHandler;

    public Server() {
        userDataAccess = new MySqlDataAccess();
        gameDataAccess = new MySqlDataAccess();
        authDataAccess = new MySqlDataAccess();
        webSocketHandler = new WebSocketHandler();
    }


    public int run(int desiredPort) {
        try {
            DatabaseManager.initializeDatabaseAndTables();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database and tables: " + e.getMessage());
        }
        Spark.port(desiredPort);
        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.

        Spark.webSocket("/chess", webSocketHandler);

        server.ClearEndpoint.setup(userDataAccess, gameDataAccess, authDataAccess);
        server.RegisterEndpoint.setup(userDataAccess, authDataAccess);
        server.LoginEndpoint.setup(authDataAccess, userDataAccess);
        server.LogoutEndpoint.setup(authDataAccess);
        server.ListGamesEndpoint.setup(gameDataAccess, authDataAccess);
        server.CreateGameServiceEndpoint.setup(authDataAccess, gameDataAccess);
        server.JoinGameEndpoint.setup(gameDataAccess, authDataAccess);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}