package server;

import dataAccess.AuthDataAccess;
import dataAccess.GameDataAccess;
import model.MessageResponse;
import service.*;
import service.Request.ListGamesRequest;
import service.Result.ListGamesResult;
import spark.Spark;
import com.google.gson.Gson;

public class ListGamesEndpoint {

    public static void setup(GameDataAccess gameDataAccess, AuthDataAccess authDataAccess) {
        ListGamesService listGamesService = new ListGamesService(gameDataAccess, authDataAccess);
        Gson gson = new Gson();

        Spark.get("/game", (req, res) -> {
            try {
                String authToken = req.headers("Authorization");
                ListGamesResult listGamesResult = listGamesService.listGames(authToken, new ListGamesRequest());
                res.type("application/json");
                res.status(200);
                return gson.toJson(listGamesResult);
            } catch (Exception e) {
                res.type("application/json");
                res.status(401);
                return gson.toJson(new MessageResponse("Error: " + e.getMessage()));
            }
        });
    }
}
