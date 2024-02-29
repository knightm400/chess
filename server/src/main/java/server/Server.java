package server;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dataAccess.*;
import model.UserData;
import org.eclipse.jetty.server.Authentication;
import service.UserService;
import spark.*;

import static spark.Spark.delete;
import static spark.Spark.post;

import service.ClearService;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");
        IUserDataAccess userDataAccess = new UserDataAccess();
        IAuthDataAccess authDataAccess = new AuthDataAccess();

        // Register your endpoints and handle exceptions here.

        delete("/db", (req, res) -> {
            try {
                ClearService clearService = new ClearService(new UserDataAccess());
                clearService.clearAllData();
                return "Database cleared";
            } catch (Exception e) {
                res.status(500);
                return "{\"message\" : \"Error: " + e.getMessage() + "\"}";
            }
        });

        post("/user", (req, res) -> {
            try {
                UserService userService = new UserService(userDataAccess, authDataAccess);
                JsonObject requestBody = (new JsonParser().parse(req.body()).getAsJsonObject());
                String username = requestBody.get("username").getAsString();
                String password = requestBody.get("password").getAsString();
                String email = requestBody.get("email").getAsString();

                UserData newUser = userService.createUser(username, password, email);
                String authToken = "someAuthToken";
                res.status(200);
                return String.format("{\"username\":\"%s\", \"authToken\":\"%s\"}", newUser.getUsername(), authToken);
            } catch (IllegalArgumentException e) {
                res.status(400);
                return "{\"message\": \"Error: bad request\"}";
            } catch (DataAccessException e) {
                res.status(403);
                return "{\"message\": \"Error: already taken\"}";
            } catch (Exception e) {
                res.status(500);
                return "{\"message\": \"Error: " + e.getMessage() + "\"}";
            }
        });

        post("/session", (req, res) -> {
            try {
                UserService userService = new UserService(userDataAccess, authDataAccess);
                JsonObject requestBody = new JsonParser().parse(req.body()).getAsJsonObject();
                String username = requestBody.get("username").getAsString();
                String password = requestBody.get("password").getAsString();

                String authToken = userService.login(username, password);

                if (authToken != null) {
                    res.status(200);
                    return String.format("{\"username\":\"%s\", \"authToken\":\"%s\"}", username, authToken);
                } else {
                    res.status(401);
                    return "{\"message\": \"Error: unauthorized\"}";
                }
            } catch (IllegalArgumentException e) {
                res.status(400);
                return "{\"message\": \"Error: description\"}";
            } catch (DataAccessException e) {
                res.status(500);
                return "{\"message\": \"Error: description\"}";
            } catch (Exception e) {
                res.status(500);
                return "{\"message\": \"Error: " + e.getMessage() + "\"}";
            }
        });
        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    public static void main(String[] args) {
        new Server().run(4567);
    }
}