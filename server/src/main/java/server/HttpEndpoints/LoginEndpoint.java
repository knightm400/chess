package server;

import dataAccess.AuthDataAccess;
import dataAccess.UserDataAccess;
import model.MessageResponse;
import service.Request.LoginRequest;
import service.Result.LoginResult;
import service.LoginService;
import spark.Spark;
import com.google.gson.Gson;

public class LoginEndpoint {

    public static void setup(AuthDataAccess authDataAccess, UserDataAccess userDataAccess) {
        LoginService loginService = new LoginService(authDataAccess, userDataAccess);
        Gson gson = new Gson();

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
    }
}
