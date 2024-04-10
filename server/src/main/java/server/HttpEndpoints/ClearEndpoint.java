package server;

import dataAccess.AuthDataAccess;
import dataAccess.GameDataAccess;
import dataAccess.UserDataAccess;
import dataAccess.DataAccessException;
import model.MessageResponse;
import service.ClearService;
import spark.Spark;
import com.google.gson.Gson;

public class ClearEndpoint {

    public static void setup(UserDataAccess userDataAccess, GameDataAccess gameDataAccess, AuthDataAccess authDataAccess) {
        ClearService clearService = new ClearService(userDataAccess, gameDataAccess, authDataAccess);
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
    }
}
