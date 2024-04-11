package server.HttpEndpoints;

import dataAccess.AuthDataAccess;
import dataAccess.DataAccessException;
import dataAccess.GameDataAccess;
import model.MessageResponse;
import service.*;
import service.Request.JoinGameRequest;
import service.Result.JoinGameResult;
import spark.Spark;
import com.google.gson.Gson;

public class JoinGameEndpoint {

    public static void setup(GameDataAccess gameDataAccess, AuthDataAccess authDataAccess) {
        JoinGameService joinGameService = new JoinGameService(gameDataAccess, authDataAccess); // Initialize appropriately
        Gson gson = new Gson();

        Spark.put("/game", (req, res) -> {
            try {
                String authToken = req.headers("Authorization");
                JoinGameRequest joinGameRequest = gson.fromJson(req.body(), JoinGameRequest.class);
                JoinGameResult joinGameResult = joinGameService.joinGame(authToken, joinGameRequest.gameID(), joinGameRequest.playerColor());
                res.type("application/json");
                res.status(200);
                return gson.toJson(joinGameResult);
            } catch (DataAccessException e) {
                res.type("application/json");
                switch (e.getMessage()) {
                    case "Unauthorized":
                        res.status(401);
                        break;
                    case "Bad Request":
                        res.status(400);
                        break;
                    case "Already taken":
                        res.status(403);
                        break;
                    default:
                        res.status(500);
                }
                return gson.toJson(new MessageResponse("Error: " + e.getMessage()));
            }
        });
    }
}
