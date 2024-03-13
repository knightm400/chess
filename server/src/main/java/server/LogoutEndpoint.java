package server;

import dataAccess.AuthDataAccess;
import model.MessageResponse;
import service.*;
import spark.Spark;
import com.google.gson.Gson;

public class LogoutEndpoint {

    public static void setup(AuthDataAccess authDataAccess) {
        LogoutService logoutService = new LogoutService(authDataAccess);
        Gson gson = new Gson();

        Spark.delete("/session", (req, res) -> {
            try {
                String authToken = req.headers("Authorization");
                LogoutRequest logoutRequest = new LogoutRequest();
                logoutService.logout(authToken, logoutRequest);
                res.type("application/json");
                res.status(200);
                return gson.toJson(new MessageResponse("Logged out successfully."));
            } catch (Exception e) {
                res.type("application/json");
                res.status(401);
                return gson.toJson(new MessageResponse("Error: " + e.getMessage()));
            }
        });
    }
}
