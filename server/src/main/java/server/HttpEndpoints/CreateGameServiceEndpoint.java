package server.HttpEndpoints;

import dataAccess.AuthDataAccess;
import dataAccess.GameDataAccess;
import model.MessageResponse;
import service.*;
import service.Request.CreateGameRequest;
import service.Result.CreateGameResult;
import spark.Spark;
import com.google.gson.Gson;

public class CreateGameServiceEndpoint {

    public static void setup(AuthDataAccess authDataAccess, GameDataAccess gameDataAccess) {
        CreateGameService createGameService = new CreateGameService(authDataAccess, gameDataAccess);
        Gson gson = new Gson();

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
                if (e.getMessage().equals("Unauthorized")) {
                    res.status(401);
                } else if (e.getMessage().equals("Bad Request")) {
                    res.status(400);
                } else {
                    res.status(500);
                }
                return gson.toJson(new MessageResponse("Error: " + e.getMessage()));
            }
        });
    }
}
