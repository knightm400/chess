package server;

import dataAccess.*;
import spark.*;
import java.sql.SQLException;

public class Server {
    private final UserDataAccess userDataAccess;
    private final GameDataAccess gameDataAccess;
    private final AuthDataAccess authDataAccess;

    public Server() {
        userDataAccess = new MySqlDataAccess();
        gameDataAccess = new MySqlDataAccess();
        authDataAccess = new MySqlDataAccess();

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

        ClearEndpoint.setup(userDataAccess, gameDataAccess, authDataAccess);
        RegisterEndpoint.setup(userDataAccess, authDataAccess);
        LoginEndpoint.setup(authDataAccess, userDataAccess);
        LogoutEndpoint.setup(authDataAccess);
        ListGamesEndpoint.setup(gameDataAccess, authDataAccess);
        CreateGameServiceEndpoint.setup(authDataAccess, gameDataAccess);
        JoinGameEndpoint.setup(gameDataAccess, authDataAccess);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}