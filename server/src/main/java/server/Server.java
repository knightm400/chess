package server;

import dataAccess.DataAccessException;
import dataAccess.MemoryAuthDataAccess;
import dataAccess.MemoryGameDataAccess;
import dataAccess.MemoryUserDataAccess;
import model.MessageResponse;
import service.*;
import spark.*;
import com.google.gson.Gson;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        // Clear Service Endpoint
        ClearService clearService = new ClearService(new MemoryUserDataAccess(), new MemoryGameDataAccess(), new MemoryAuthDataAccess());
        Gson gson = new Gson();

        Spark.delete("/db", (req, res) -> {
            try {
                clearService.clearAll();
                res.status(200);
                return gson.toJson(new MessageResponse("All data cleared."));
            } catch (DataAccessException e) {
                res.status(500);
                return gson.toJson(new MessageResponse("Error clearing the database: " + e.getMessage()));
            }
        }, gson::toJson);


        //Register Service Endpoint
        MemoryUserDataAccess userDataAccess = new MemoryUserDataAccess();
        MemoryAuthDataAccess authDataAccess = new MemoryAuthDataAccess();
        RegisterService registerService = new RegisterService(userDataAccess, authDataAccess);

        Spark.post("/user", (req, res) -> {
            try {
                RegisterRequest registerRequest = gson.fromJson(req.body(), RegisterRequest.class);
                RegisterResult registerResult = registerService.register(registerRequest);

                if (registerResult.authToken() != null && !registerResult.authToken().isEmpty()) {
                    res.status(200);
                    return gson.toJson(registerResult);
                } else {
                    res.status(400);
                    return gson.toJson(new MessageResponse("Error: bad request or username already taken"));
                }
            } catch (Exception e) {
                res.status(500);
                return gson.toJson(new MessageResponse("Error: " + e.getMessage()));
            }
        }, gson::toJson);


        //Login Endpoint

        LoginService loginService = new LoginService(authDataAccess, userDataAccess);

        Spark.post("/session", (req, res) -> {
            try {
                LoginRequest loginRequest = gson.fromJson(req.body(), LoginRequest.class);
                LoginResult loginResult = loginService.login(loginRequest);

                if (loginResult.authToken() != null && !loginResult.authToken().isEmpty()) {
                    res.status(200);
                    return gson.toJson(loginResult);
                } else {
                    res.status(401);
                    return gson.toJson(new MessageResponse("Error: unauthorized"));
                }
            } catch (Exception e) {
                res.status(500);
                return gson.toJson(new MessageResponse("Error: " + e.getMessage()));
            }
        }, gson::toJson);


        //Logout Service Endpoint

        LogoutService logoutService = new LogoutService(authDataAccess);

        Spark.delete("/session", (req, res) -> {
            try {
                String authToken = req.headers("Authorization");
                LogoutRequest logoutRequest = new LogoutRequest(authToken);
                logoutService.logout(logoutRequest);
                res.status(200);
                return gson.toJson(new MessageResponse("Logged out successfully."));
            } catch (Exception e) {
                res.status(401);
                return gson.toJson(new MessageResponse("Error: " + e.getMessage()));
            }
        }, gson::toJson);


        //ListGames Endpoint

        MemoryGameDataAccess gameDataAccess = new MemoryGameDataAccess();
        ListGamesService listGamesService = new ListGamesService(gameDataAccess);

        Spark.get("/game", (req, res) -> {
            try {
                ListGamesResult listGamesResult = listGamesService.listGames(new ListGamesRequest());
                res.status(200);
                return gson.toJson(listGamesResult);
            } catch (Exception e) {
                res.status(401);
                return gson.toJson(new MessageResponse("Error: " + e.getMessage()));
            }
        }, gson::toJson);


        //CreateGame Endpoint

        CreateGameService createGameService = new CreateGameService(authDataAccess, gameDataAccess);

        Spark.post("/game", (req, res) -> {
            try {
                String authToken = req.headers("Authorization");
                CreateGameRequest createGameRequest = new CreateGameRequest(authToken, gson.fromJson(req.body(), CreateGameRequest.class).gameName());
                CreateGameResult createGameResult = createGameService.createGame(createGameRequest);
                res.status(200);
                return gson.toJson(createGameResult);
            } catch (Exception e) {
                res.status(500);
                return gson.toJson(new MessageResponse("Error: " + e.getMessage()));
            }
        }, gson::toJson);


        //JoinGame Endpoint

        JoinGameService joinGameService = new JoinGameService(gameDataAccess, authDataAccess); // Initialize appropriately

        Spark.put("/game", (req, res) -> {
            try {
                String authToken = req.headers("Authorization");
                JoinGameRequest joinGameRequest = gson.fromJson(req.body(), JoinGameRequest.class);
                JoinGameResult joinGameResult = joinGameService.joinGame(authToken, joinGameRequest.gameID(), joinGameRequest.playerColor());
                res.status(200);
                return gson.toJson(joinGameResult);
            } catch (Exception e) {
                res.status(401);  
                return gson.toJson(new MessageResponse("Error: " + e.getMessage()));
            }
        }, gson::toJson);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}