package ui;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import com.google.gson.Gson;
import model.GameData;
import model.MessageResponse;
import service.Result.*;

public class ServerFacade {
    private static final String SERVER_BASE_URL = "http://localhost:8080";
    private static final Gson gson = new Gson();
    private static final Logger logger = Logger.getLogger(ServerFacade.class.getName());

    static {
        try {
            FileHandler fileHandler = new FileHandler("ServerFacade.log", true);
            logger.addHandler(fileHandler);
        } catch (Exception e) {
            logger.severe("Failed to initialize log file handler.");
        }
    }

    public String clearData() throws Exception {
        logger.info("Attempting to clear data...");
        String endpoint = "/db";
        URL url = new URL(SERVER_BASE_URL + endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("DELETE");
        connection.setRequestProperty("Content-Type", "application/json");

        int responseCode = connection.getResponseCode();
        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                responseCode >= HttpURLConnection.HTTP_BAD_REQUEST ? connection.getErrorStream() : connection.getInputStream(), "utf-8"))) {
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }

        if (responseCode == 200) {
            logger.info("Data cleared successfully.");
            return "All data cleared";
        } else {
            MessageResponse messageResponse = gson.fromJson(response.toString(), MessageResponse.class);
            logger.severe("Failed to clear data: " + messageResponse.getMessage());
            throw new Exception("Error clearing the database: " + messageResponse.getMessage());
        }
    }

    public String register(String username, String password, String email) throws Exception {
        logger.info("Attempting to register a new user.");
        String endpoint = "/user";
        URL url = new URL(SERVER_BASE_URL + endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");

        Map<String, String> requestBodyMap = new HashMap<>();
        requestBodyMap.put("username", username);
        requestBodyMap.put("password", password);
        requestBodyMap.put("email", email);
        String requestBody = gson.toJson(requestBodyMap);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = requestBody.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }

        int responseCode = connection.getResponseCode();
        if (responseCode == 200) {
            RegisterResult result = gson.fromJson(response.toString(), RegisterResult.class);
            logger.info("User registered successfully: " + username);
            return result.authToken();
        } else {
            logger.warning("Registration failed: " + responseCode);
            throw new Exception("Registration failed with status: " + responseCode);
        }
    }

    public String login(String username, String password) throws Exception {
        logger.info("Attempting to login user.");
        String endpoint = "/session";
        URL url = new URL(SERVER_BASE_URL + endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");

        Map<String, String> requestBodyMap = new HashMap<>();
        requestBodyMap.put("username", username);
        requestBodyMap.put("password", password);
        String requestBody = gson.toJson(requestBodyMap);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = requestBody.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), "utf-8"))) {
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }

        int responseCode = connection.getResponseCode();
        if (responseCode == 200) {
            logger.info("User logged in successfully: " + username);
            LoginResult result = gson.fromJson(response.toString(), LoginResult.class);
            return result.authToken();
        } else if (responseCode == 401) {
            throw new Exception("Login failed: unauthorized (wrong username or password)");
        } else {
            logger.warning("Login failed: " + responseCode);
            throw new Exception("Login failed with status: " + responseCode);
        }
    }

    public String logout(String authToken) throws Exception {
        logger.info("Attempting to log out user.");
        String endpoint = "/session";
        URL url = new URL(SERVER_BASE_URL + endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("DELETE");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", authToken);

        int responseCode = connection.getResponseCode();
        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                responseCode >= HttpURLConnection.HTTP_BAD_REQUEST ? connection.getErrorStream() : connection.getInputStream(), "utf-8"))) {
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }

        if (responseCode == 200) {
            logger.info("User logged out successfully.");
            return "Logged out successfully.";
        } else {
            MessageResponse messageResponse = gson.fromJson(response.toString(), MessageResponse.class);
            logger.warning("Logout failed: " + responseCode);
            throw new Exception("Logout failed: " + messageResponse.getMessage());
        }
    }

    public List<GameData> listGames(String authToken) throws Exception {
        logger.info("Attempting to list games.");
        String endpoint = "/game";
        URL url = new URL(SERVER_BASE_URL + endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", authToken);

        int responseCode = connection.getResponseCode();
        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                responseCode >= HttpURLConnection.HTTP_BAD_REQUEST ? connection.getErrorStream() : connection.getInputStream(), "utf-8"))) {
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }

        if (responseCode == 200) {
            ListGamesResult listGamesResult = gson.fromJson(response.toString(), ListGamesResult.class);
            logger.info("Games listed successfully.");
            return listGamesResult.getGames();
        } else {
            logger.warning("Failed to list games: " + responseCode);
            throw new Exception("Failed to list games: HTTP error code " + responseCode);
        }
    }

    public GameData createGame(String authToken, String gameName) throws Exception {
        logger.info("Attempting to create a new game.");
        String endpoint = "/game";
        URL url = new URL(SERVER_BASE_URL + endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", authToken);

        String jsonRequestBody = gson.toJson(Map.of("gameName", gameName));

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonRequestBody.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        StringBuilder response = new StringBuilder();
        int responseCode = connection.getResponseCode();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                responseCode >= HttpURLConnection.HTTP_BAD_REQUEST ? connection.getErrorStream() : connection.getInputStream(), "utf-8"))) {
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }

        if (responseCode == 200) {
            CreateGameResult createGameResult = gson.fromJson(response.toString(), CreateGameResult.class);
            logger.info("Game created successfully: " + gameName);
            return new GameData(createGameResult.gameID(), null, null, null, null, null, null); // Modify according to your GameData constructor
        } else {
            logger.warning("Failed to create game: " + responseCode);
            throw new Exception("Failed to create game: HTTP error code " + responseCode);
        }
    }

    public JoinGameResult joinGame(String authToken, Integer gameID, String playerColor) throws Exception {
        logger.info("Attempting to join a game.");
        String endpoint = "/game";
        URL url = new URL(SERVER_BASE_URL + endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("PUT");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", authToken);

        String jsonRequestBody = gson.toJson(Map.of("gameID", gameID, "playerColor", playerColor));

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonRequestBody.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        StringBuilder response = new StringBuilder();
        int responseCode = connection.getResponseCode();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                responseCode >= HttpURLConnection.HTTP_BAD_REQUEST ? connection.getErrorStream() : connection.getInputStream(), "utf-8"))) {
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }

        if (responseCode == 200) {
            JoinGameResult joinGameResult = gson.fromJson(response.toString(), JoinGameResult.class);
            logger.info("Joined game successfully: Game ID " + gameID);
            return joinGameResult;
        } else {
            logger.warning("Failed to join game: " + responseCode);
            throw new Exception("Failed to join game: HTTP error code " + responseCode);
        }
    }
}

