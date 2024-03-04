package server;

import com.google.gson.Gson;
import dataAccess.AuthDataAccess;
import dataAccess.DataAccessException;
import dataAccess.GameDataAccess;
import dataAccess.UserDataAccess;
import model.AuthData;
import model.GameData;
import model.UserData;
import service.*;
import spark.*;

import java.util.List;

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


        Spark.post("/user", (request, response) -> {
            Gson gson = new Gson();
            UserData userData = gson.fromJson(request.body(), UserData.class);

            try {
                AuthData authData = registerService.register(userData);
                response.status(200);
                return gson.toJson(authData);
            } catch (DataAccessException e) {
                response.status(400);
                return "User registration failed: " + e.getMessage();
            }
        });

        Spark.post("/session", (request, response) -> {
            String username = request.queryParams("username");
            String password = request.queryParams("password");

            try {
                AuthData authData = loginService.login(username, password);
                response.status(200);
                return "Login successful, token: " + authData.getAuthToken();
            } catch (DataAccessException e) {
                response.status(401);
                return "Login failed: " + e.getMessage();
            }
        });

        Spark.delete("/session", (request, response) -> {
            String authToken = request.queryParams("authToken");
            try {
                logoutService.logout(authToken);
                response.status(200);
                return "Logout successful";
            } catch (DataAccessException e) {
                response.status(401);
                return "Logout failed: " + e.getMessage();
            }
        });

        Spark.get("/game", (request, response) -> {
            String authToken = request.queryParams("authToken");
            try {
                List<GameData> gamesList = listGamesService.listGames(authToken);
                response.status(200);
                response.type("application/json");
                return new Gson().toJson(gamesList);
            } catch (Exception e) {
                response.status(401);
                return "Failed to list games: " + e.getMessage();
            }
        });

        Spark.post("/game", (request, response) -> {
            String authToken = request.queryParams("authToken");
            String gameName = request.queryParams("gameName");
            try {
                GameData newGame = createGameService.createGame(authToken, gameName);
                response.status(200);
                response.type("application/json");
                return new Gson().toJson(newGame);
            } catch (Exception e) {
                response.status(400);
                return "Failed to create game: " + e.getMessage();
            }
        });

        Spark.put("/game", (request, response) -> {
            String authToken = request.queryParams("authToken");
            String gameID = request.queryParams("gameID");
            String playerColor = request.queryParams("playerColor");
            try {
                JoinGameResult result = joinGameService.joinGame(authToken, gameID, playerColor);
                response.status(200);
                response.type("application/json");
                return new Gson().toJson(result);
            } catch (Exception e) {
                response.status(400);
                return "Failed to join game: " + e.getMessage();
            }
        });

        Spark.delete("/db", (request, response) -> {
            clearService.clearAll();
            response.status(200); // OK
            return "All data cleared successfully";
        });

        Spark.exception(Exception.class, (exception, request, response) -> {
            response.status(500);
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