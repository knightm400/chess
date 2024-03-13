package server;

import dataAccess.*;
import spark.*;

public class Server {
    private UserDataAccess userDataAccess;
    private GameDataAccess gameDataAccess;
    private AuthDataAccess authDataAccess;

    public Server() {
        userDataAccess = new MemoryUserDataAccess();
        gameDataAccess = new MemoryGameDataAccess();
        authDataAccess = new MemoryAuthDataAccess();
    }

    public int run(int desiredPort) {
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