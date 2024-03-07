package server;

import dataAccess.DataAccessException;
import dataAccess.MemoryAuthDataAccess;
import dataAccess.MemoryGameDataAccess;
import dataAccess.MemoryUserDataAccess;
import model.MessageResponse;
import service.*;
import spark.*;
import com.google.gson.Gson;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        // Clear Service Endpoint
        ClearService clearService = new ClearService(new MemoryUserDataAccess(), new MemoryGameDataAccess(), new MemoryAuthDataAccess());
        Gson gson = new Gson();

        Spark.delete("/db", (req, res) -> {
            try {
                clearService.clearAll();
                res.status(200);
                return gson.toJson(new MessageResponse("All data cleared."));
            } catch (DataAccessException e) {
                res.status(500);
                return gson.toJson(new MessageResponse("Error clearing the database: " + e.getMessage()));
            }
        }, gson::toJson);


        //Register Service Endpoint
        MemoryUserDataAccess userDataAccess = new MemoryUserDataAccess();
        MemoryAuthDataAccess authDataAccess = new MemoryAuthDataAccess();
        RegisterService registerService = new RegisterService(userDataAccess, authDataAccess);

        Spark.post("/user", (req, res) -> {
            try {
                RegisterRequest registerRequest = gson.fromJson(req.body(), RegisterRequest.class);
                RegisterResult registerResult = registerService.register(registerRequest);

                if (registerResult.authToken() != null && !registerResult.authToken().isEmpty()) {
                    res.status(200);
                    return gson.toJson(registerResult);
                } else {
                    res.status(400);
                    return gson.toJson(new MessageResponse("Error: bad request or username already taken"));
                }
            } catch (Exception e) {
                res.status(500);
                return gson.toJson(new MessageResponse("Error: " + e.getMessage()));
            }
        }, gson::toJson);


        //Login Endpoint

        LoginService loginService = new LoginService(authDataAccess, userDataAccess);

        Spark.post("/session", (req, res) -> {
            try {
                LoginRequest loginRequest = gson.fromJson(req.body(), LoginRequest.class);
                LoginResult loginResult = loginService.login(loginRequest);

                if (loginResult.authToken() != null && !loginResult.authToken().isEmpty()) {
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
        }, gson::toJson);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}