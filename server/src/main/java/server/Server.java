package server;

import dataAccess.*;
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
        MemoryAuthDataAccess authDataAccess = new MemoryAuthDataAccess();

        Gson gson = new Gson();

        Spark.delete("/db", (req, res) -> {
            try {
                clearService.clearAll(); // No authToken needed
                res.type("application/json");
                res.status(200);
                return gson.toJson(new MessageResponse("All data cleared"));
            } catch (DataAccessException e) {
                res.type("application/json");
                res.status(500);
                return gson.toJson(new MessageResponse("Error clearing the database: " + e.getMessage()));
            }
        });


        //Register Service Endpoint
        MemoryUserDataAccess userDataAccess = new MemoryUserDataAccess();
        RegisterService registerService = new RegisterService(userDataAccess, authDataAccess);

        Spark.post("/user", (req, res) -> {
            try {
                RegisterRequest registerRequest = gson.fromJson(req.body(), RegisterRequest.class);
                RegisterResult registerResult = registerService.register(registerRequest);
                res.type("application/json");
                res.status(200);
                return gson.toJson(registerResult);
            } catch (DataAccessException e) {
                res.type("application/json");
                if (e.getMessage().equals("Username already taken")) {
                    res.status(403);
                } else if (e.getMessage().equals("Invalid registration information provided")) {
                    res.status(400);
                } else {
                    res.status(500);
                }
                return gson.toJson(new MessageResponse("Error: " + e.getMessage()));
            }
        });


        //Login Endpoint

        LoginService loginService = new LoginService(authDataAccess, userDataAccess);

        Spark.post("/session", (req, res) -> {
            res.type("application/json");
            try {
                LoginRequest loginRequest = gson.fromJson(req.body(), LoginRequest.class);
                LoginResult loginResult = loginService.login(loginRequest);

                if (loginResult != null && loginResult.authToken() != null && !loginResult.authToken().isEmpty()) {
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
        });


        //Logout Service Endpoint

        LogoutService logoutService = new LogoutService(authDataAccess);

        Spark.delete("/session", (req, res) -> {
            try {
                String authToken = req.headers("Authorization");
                LogoutRequest logoutRequest = new LogoutRequest(authToken);
                logoutService.logout(logoutRequest);
                res.type("application/json");
                res.status(200);
                return gson.toJson(new MessageResponse("Logged out successfully."));
            } catch (Exception e) {
                res.type("application/json");
                res.status(401);
                return gson.toJson(new MessageResponse("Error: " + e.getMessage()));
            }
        });


        //ListGames Endpoint

        MemoryGameDataAccess gameDataAccess = new MemoryGameDataAccess();
        ListGamesService listGamesService = new ListGamesService(gameDataAccess);

        Spark.get("/game", (req, res) -> {
            try {
                ListGamesResult listGamesResult = listGamesService.listGames(new ListGamesRequest());
                res.type("application/json");
                res.status(200);
                return gson.toJson(listGamesResult);
            } catch (Exception e) {
                res.type("application/json");
                res.status(401);
                return gson.toJson(new MessageResponse("Error: " + e.getMessage()));
            }
        });


        //CreateGame Endpoint

        CreateGameService createGameService = new CreateGameService(authDataAccess, gameDataAccess);

        Spark.post("/game", (req, res) -> {
            try {
                String authToken = req.headers("Authorization");
                CreateGameRequest createGameRequest = new CreateGameRequest(authToken, gson.fromJson(req.body(), CreateGameRequest.class).gameName());
                CreateGameResult createGameResult = createGameService.createGame(createGameRequest);
                res.type("application/json");
                res.status(200);
                return gson.toJson(createGameResult);
            } catch (Exception e) {
                res.type("application/json");
                res.status(500);
                return gson.toJson(new MessageResponse("Error: " + e.getMessage()));
            }
        });


        //JoinGame Endpoint

        JoinGameService joinGameService = new JoinGameService(gameDataAccess, authDataAccess); // Initialize appropriately

        Spark.put("/game", (req, res) -> {
            try {
                String authToken = req.headers("Authorization");
                JoinGameRequest joinGameRequest = gson.fromJson(req.body(), JoinGameRequest.class);
                JoinGameResult joinGameResult = joinGameService.joinGame(authToken, joinGameRequest.gameID(), joinGameRequest.playerColor());
                res.type("application/json");
                res.status(200);
                return gson.toJson(joinGameResult);
            } catch (Exception e) {
                res.type("application/json");
                res.status(400);
                return gson.toJson(new MessageResponse("Error: " + e.getMessage()));
            }
        });

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}