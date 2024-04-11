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

        Spark.webSocket("/connect", webSocketHandler);

        server.HttpEndpoints.ClearEndpoint.setup(userDataAccess, gameDataAccess, authDataAccess);
        server.HttpEndpoints.RegisterEndpoint.setup(userDataAccess, authDataAccess);
        server.HttpEndpoints.LoginEndpoint.setup(authDataAccess, userDataAccess);
        server.HttpEndpoints.LogoutEndpoint.setup(authDataAccess);
        server.HttpEndpoints.ListGamesEndpoint.setup(gameDataAccess, authDataAccess);
        server.HttpEndpoints.CreateGameServiceEndpoint.setup(authDataAccess, gameDataAccess);
        server.HttpEndpoints.JoinGameEndpoint.setup(gameDataAccess, authDataAccess);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}