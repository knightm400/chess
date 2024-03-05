package server;

import com.google.gson.Gson;
import dataAccess.*;
import model.*;
import service.*;
import spark.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server {
    private final AuthService authService;
    private final ClearService clearService;
    private final CreateGameService createGameService;
    private final ListGamesService listGamesService;
    private final LoginService loginService;
    private final LogoutService logoutService;
    private final RegisterService registerService;
    private final JoinGameService joinGameService;

    public Server() {
        this.authService = new AuthService(new UserDataAccess());
        this.clearService = new ClearService(new UserDataAccess(), new GameDataAccess());
        this.createGameService = new CreateGameService(new GameDataAccess());
        this.listGamesService = new ListGamesService(new GameDataAccess());
        this.loginService = new LoginService(new UserDataAccess());
        this.logoutService = new LogoutService(new AuthDataAccess());
        this.registerService = new RegisterService(new UserDataAccess());
        this.joinGameService = new JoinGameService(new GameDataAccess());
    }


    public int run(int desiredPort) {
        Spark.port(desiredPort);
        Spark.staticFiles.location("web");
        Gson gson = new Gson();

        Spark.post("/user", (request, response) -> {
            UserData userData = gson.fromJson(request.body(), UserData.class);
            try {
                AuthData authData = registerService.register(userData);
                response.status(200); // OK
                response.type("application/json");
                return gson.toJson(new UserResponse(authData.getUsername(), authData.getAuthToken()));
            } catch (DataAccessException e) {
                response.status(400); // Bad Request
                return gson.toJson(Map.of("message", "User registration failed: " + e.getMessage()));
            }
        });


        Spark.post("/session", (request, response) -> {
            UserData userData = gson.fromJson(request.body(), UserData.class);
            try {
                // Attempt to login with the provided credentials
                AuthData authData = loginService.login(userData.getUsername(), userData.getPassword());
                response.status(200); // OK
                response.type("application/json"); // Ensure the response type is set to JSON
                // Return the AuthData object as a JSON string
                return gson.toJson(new UserResponse(authData.getUsername(), authData.getAuthToken()));
            } catch (DataAccessException e) {
                response.status(401); // Unauthorized
                response.type("application/json");
                // Return an error message as a JSON string
                return gson.toJson(Map.of("message", "Login failed: " + e.getMessage()));
            }
        }, gson::toJson);

        Spark.delete("/session", (request, response) -> {
            String authToken = request.params(":authToken");
            try {
                logoutService.logout(authToken);
                response.status(200); // OK
                return gson.toJson(Map.of("message", "Logout successful"));
            } catch (DataAccessException e) {
                response.status(401); // Unauthorized
                return gson.toJson(Map.of("message", "Logout failed: " + e.getMessage()));
            }
        }, gson::toJson);

        Spark.get("/game", (request, response) -> {
            String authToken = request.queryParams("authToken");
            try {
                List<GameData> gamesList = listGamesService.listGames(authToken);
                response.status(200); // OK
                response.type("application/json");
                return gson.toJson(gamesList);
            } catch (Exception e) {
                response.status(401); // Unauthorized
                return gson.toJson(Map.of("message", "Failed to list games: " + e.getMessage()));
            }
        }, gson::toJson);

        Spark.post("/game", (request, response) -> {
            String authToken = request.queryParams("authToken");
            String gameName = request.queryParams("gameName");
            try {
                GameData newGame = createGameService.createGame(authToken, gameName);
                response.status(200); // OK
                response.type("application/json");
                return gson.toJson(newGame);
            } catch (Exception e) {
                response.status(400); // Bad Request
                return gson.toJson(Map.of("message", "Failed to create game: " + e.getMessage()));
            }
        }, gson::toJson);


        Spark.put("/game", (request, response) -> {
            String authToken = request.queryParams("authToken");
            String gameID = request.queryParams("gameID");
            String playerColor = request.queryParams("playerColor");
            try {
                JoinGameResult result = joinGameService.joinGame(authToken, gameID, playerColor);
                response.status(200); // OK
                response.type("application/json");
                return gson.toJson(result);
            } catch (Exception e) {
                response.status(400); // Bad Request
                return gson.toJson(Map.of("message", "Failed to join game: " + e.getMessage()));
            }
        }, gson::toJson);

        Spark.delete("/db", (request, response) -> {
            try {
                clearService.clearAll();
                response.status(200); // OK status
                response.type("application/json"); // Set the type of response to JSON
                Map<String, String> result = new HashMap<>();
                result.put("message", "All data cleared successfully");
                return gson.toJson(result); // Return a JSON object
            } catch (Exception e) {
                response.status(500); // Internal Server Error
                return gson.toJson(Map.of("message", "An error occurred: " + e.getMessage())); // Return error message in JSON format
            }
        });



        Spark.exception(Exception.class, (exception, request, response) -> {
            response.status(500); // Internal Server Error
            response.type("application/json"); // Set response type
            Map<String, String> errorResponse = Map.of("message", "An error occurred: " + exception.getMessage());
            response.body(new Gson().toJson(errorResponse)); // Convert map to JSON
        });

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private static class UserResponse {
        private final String username;
        private final String authToken;

        public UserResponse(String username, String authToken) {
            this.username = username;
            this.authToken = authToken;
        }

        // Getters for GSON to serialize
        public String getUsername() {
            return username;
        }

        public String getAuthToken() {
            return authToken;
        }
    }
}