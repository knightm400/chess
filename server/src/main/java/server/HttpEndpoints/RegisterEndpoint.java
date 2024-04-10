package server;

import dataAccess.AuthDataAccess;
import dataAccess.UserDataAccess;
import dataAccess.DataAccessException;
import model.MessageResponse;
import service.Request.RegisterRequest;
import service.Result.RegisterResult;
import service.RegisterService;
import spark.Spark;
import com.google.gson.Gson;

public class RegisterEndpoint {

    public static void setup(UserDataAccess userDataAccess, AuthDataAccess authDataAccess) {
        RegisterService registerService = new RegisterService(userDataAccess, authDataAccess);
        Gson gson = new Gson();

        Spark.post("/user", (req, res) -> {
            try {
                RegisterRequest registerRequest = gson.fromJson(req.body(), RegisterRequest.class);
                if (registerRequest.username() == null || registerRequest.username().trim().isEmpty() ||
                        registerRequest.password() == null || registerRequest.password().trim().isEmpty() ||
                        registerRequest.email() == null || registerRequest.email().trim().isEmpty()) {

                    res.type("application/json");
                    res.status(400);
                    return gson.toJson(new MessageResponse("Error: Username, password, and email cannot be empty."));
                }
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
    }
}
