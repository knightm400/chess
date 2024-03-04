package server;

import com.google.gson.Gson;
import dataAccess.DataAccessException;
import model.AuthData;
import model.GameData;
import model.UserData;
import spark.*;
import service.*;

import java.util.List;

public class Server {
    private AuthService authService;
    private ClearService clearService;
    private CreateGameService createGameService;
    private ListGamesService listGamesService;
    private LoginService loginService;
    private LogoutService logoutService;
    private RegisterService registerService;
    private Join

    public Server(AuthService authService, ClearService clearService, CreateGameService createGameService,
                  ListGamesService listGamesService, LoginService loginService, LogoutService logoutService,
                  RegisterService registerService) {
        this.authService = authService;
        this.clearService = clearService;
        this.createGameService = createGameService;
        this.listGamesService = listGamesService;
        this.loginService = loginService;
        this.logoutService = logoutService;
        this.registerService = registerService;
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.

        Spark.post("/user", (request, response) -> {
            Gson gson = new Gson();
            UserData userData = gson.fromJson(request.body(), UserData.class);

            try {
                AuthData authData = registerService.register(userData);
                response.status(200); // OK
                return gson.toJson(authData);
            } catch (DataAccessException e) {
                response.status(400); // Bad Request
                return "User registration failed: " + e.getMessage();
            }
        });

        // Define the endpoint for user login
        Spark.post("/session", (request, response) -> {
            String username = request.queryParams("username");
            String password = request.queryParams("password");

            try {
                AuthData authData = loginService.login(username, password);
                response.status(200); // OK
                return "Login successful, token: " + authData.getAuthToken();
            } catch (DataAccessException e) {
                response.status(401); // Unauthorized
                return "Login failed: " + e.getMessage();
            }
        });

        // Define the endpoint for user logout
        Spark.delete("/session", (request, response) -> {
            String authToken = request.queryParams("authToken");
            try {
                logoutService.logout(authToken);
                response.status(200); // OK
                return "Logout successful";
            } catch (DataAccessException e) {
                response.status(401); // Unauthorized
                return "Logout failed: " + e.getMessage();
            }
        });

        // Define the endpoint for listing games
        Spark.get("/game", (request, response) -> {
            String authToken = request.queryParams("authToken");
            try {
                List<GameData> gamesList = listGamesService.listGames(authToken);
                response.status(200); // OK
                response.type("application/json");
                return new Gson().toJson(gamesList);
            } catch (Exception e) { // Replace Exception with your custom exception if any
                response.status(401); // Unauthorized or any appropriate error code
                return "Failed to list games: " + e.getMessage();
            }
        });

        // Define the endpoint for creating a game
        Spark.post("/game", (request, response) -> {
            String authToken = request.queryParams("authToken");
            String gameName = request.queryParams("gameName");
            try {
                GameData newGame = createGameService.createGame(authToken, gameName);
                response.status(200); // OK
                response.type("application/json");
                return new Gson().toJson(newGame);
            } catch (Exception e) { // Replace Exception with a specific exception
                response.status(400); // Bad Request or other appropriate error code
                return "Failed to create game: " + e.getMessage();
            }
        });

        // Define the endpoint for joining a game
        Spark.put("/game", (request, response) -> {
            String authToken = request.queryParams("authToken");
            String gameID = request.queryParams("gameID");
            String playerColor = request.queryParams("playerColor"); // Assume playerColor can be 'WHITE' or 'BLACK'
            try {
                JoinGameResult result = joinGameService.joinGame(authToken, gameID, playerColor);
                response.status(200); // OK
                response.type("application/json");
                return new Gson().toJson(result);
            } catch (Exception e) { // Replace with specific exceptions as needed
                response.status(400); // Bad Request or other appropriate status code
                return "Failed to join game: " + e.getMessage();
            }
        });

        // Define the endpoint for clearing application data
        Spark.delete("/db", (request, response) -> {
            // Logic to clear all data
        });

        // Handle exceptions here
        Spark.exception(Exception.class, (exception, request, response) -> {
            response.status(500); // Internal Server Error
            response.body("An error occurred: " + exception.getMessage());
        });

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}