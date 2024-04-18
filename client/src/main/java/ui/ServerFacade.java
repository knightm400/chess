package ui;

import java.io.BufferedReader;
import java.io.IOException;
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
import com.google.gson.reflect.TypeToken;
import model.GameData;
import model.MessageResponse;

import javax.websocket.Session;
import ui.WebSocket.WebSocketClientHandler;
public class ServerFacade {
    private String SERVER_BASE_URL = "http://localhost:8080";
    private static final Gson gson = new Gson();
    private static final Logger logger = Logger.getLogger(ServerFacade.class.getName());
    private String authToken =  null;
    private WebSocketClientHandler webSocketClientHandler;

    public void setAuthToken(String newAuthToken) {
        this.authToken = newAuthToken;
    }

    static {
        try {
            FileHandler fileHandler = new FileHandler("ServerFacade.log", true);
            logger.addHandler(fileHandler);
        } catch (Exception e) {
            logger.severe("Failed to initialize log file handler.");
        }
    }

    public ServerFacade() {
        webSocketClientHandler = new WebSocketClientHandler();
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
        HttpURLConnection connection = setupConnection(url, "POST", true);

        Map<String, String> requestBodyMap = new HashMap<>();
        requestBodyMap.put("username", username);
        requestBodyMap.put("password", password);
        requestBodyMap.put("email", email);

        sendRequest(connection, requestBodyMap);
        return handleResponse(connection, "Registration successful.", "Registration failed");
    }

    public String login(String username, String password) throws Exception {
        logger.info("Attempting to login user.");
        String endpoint = "/session";
        URL url = new URL(SERVER_BASE_URL + endpoint);
        HttpURLConnection connection = setupConnection(url, "POST", true);

        Map<String, String> requestBodyMap = new HashMap<>();
        requestBodyMap.put("username", username);
        requestBodyMap.put("password", password);

        sendRequest(connection, requestBodyMap);
        return handleResponse(connection, "Login successful.", "Login failed");
    }

    public String logout() throws Exception {
        logger.info("Attempting to log out user.");
        String endpoint = "/session";
        URL url = new URL(SERVER_BASE_URL + endpoint);
        HttpURLConnection connection = setupConnection(url, "DELETE", false);

        return handleResponse(connection, "Logged out successfully.", "Logout failed");
    }

    private HttpURLConnection setupConnection(URL url, String method, boolean doOutput) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        connection.setDoOutput(doOutput);
        connection.setRequestProperty("Content-Type", "application/json");
        if (this.authToken != null) {
            connection.setRequestProperty("Authorization", this.authToken);
        }
        return connection;
    }

    private void sendRequest(HttpURLConnection connection, Map<String, String> requestBodyMap) throws IOException {
        String requestBody = gson.toJson(requestBodyMap);
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = requestBody.getBytes("utf-8");
            os.write(input, 0, input.length);
        }
    }

    private String handleResponse(HttpURLConnection connection, String successMessage, String failureMessage) throws Exception {
        int responseCode = connection.getResponseCode();
        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                responseCode >= HttpURLConnection.HTTP_BAD_REQUEST ? connection.getErrorStream() : connection.getInputStream(), "utf-8"))) {
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }

        if (responseCode == HttpURLConnection.HTTP_OK) {
            logger.info(successMessage);
            return response.toString();
        } else {
            MessageResponse messageResponse = gson.fromJson(response.toString(), MessageResponse.class);
            logger.warning(failureMessage + ": " + messageResponse.getMessage());
            throw new Exception(failureMessage + ": " + messageResponse.getMessage());
        }
    }


    public List<GameData> listGames() throws Exception {
        logger.info("Attempting to list games.");
        String endpoint = "/game";
        URL url = new URL(SERVER_BASE_URL + endpoint);
        HttpURLConnection connection = setupConnection(url, "GET", false);

        String response = handleResponse(connection, "Games listed successfully.", "Failed to list games");
        return gson.fromJson(response, new TypeToken<List<GameData>>() {}.getType());
    }

    public GameData createGame(String authToken, String gameName) throws Exception {
        logger.info("Attempting to create a new game.");
        String endpoint = "/game";
        URL url = new URL(SERVER_BASE_URL + endpoint);
        HttpURLConnection connection = setupConnection(url, "POST", true);

        if (authToken != null && !authToken.isEmpty()) {
            connection.setRequestProperty("Authorization", authToken);
        }

        Map<String, String> requestBodyMap = new HashMap<>();
        requestBodyMap.put("gameName", gameName);

        String requestBody = gson.toJson(requestBodyMap);
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = requestBody.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        int responseCode = connection.getResponseCode();
        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                responseCode >= HttpURLConnection.HTTP_BAD_REQUEST ? connection.getErrorStream() : connection.getInputStream(), "utf-8"))) {
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }

        if (responseCode == HttpURLConnection.HTTP_OK) {
            logger.info("Game created successfully: " + gameName);
            return gson.fromJson(response.toString(), GameData.class);
        } else {
            MessageResponse messageResponse = gson.fromJson(response.toString(), MessageResponse.class);
            logger.warning("Failed to create game: " + responseCode + " - " + messageResponse.getMessage());
            throw new Exception("Failed to create game: " + messageResponse.getMessage());
        }
    }

    public GameData joinGame(String authToken, Integer gameID, String playerColor) throws Exception {
        logger.info("Attempting to join a game.");
        String endpoint = "/game/join";
        URL url = new URL(SERVER_BASE_URL + endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("PUT");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");
        if (authToken != null) {
            connection.setRequestProperty("Authorization", authToken);
        }

        Map<String, Object> jsonRequestBody = new HashMap<>();
        jsonRequestBody.put("gameID", gameID);
        jsonRequestBody.put("playerColor", playerColor);

        String requestBody = gson.toJson(jsonRequestBody);
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = requestBody.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        int responseCode = connection.getResponseCode();
        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                responseCode >= HttpURLConnection.HTTP_BAD_REQUEST ? connection.getErrorStream() : connection.getInputStream(), "utf-8"))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }

        if (responseCode == 200) {
            logger.info("Joined game successfully: Game ID " + gameID);
            return gson.fromJson(response.toString(), GameData.class);
        } else {
            MessageResponse messageResponse = gson.fromJson(response.toString(), MessageResponse.class);
            logger.warning("Failed to join game: " + responseCode + " - " + messageResponse.getMessage());
            throw new Exception("Failed to join game: " + messageResponse.getMessage());
        }
    }



    public boolean joinGameAsObserver(Integer gameId) throws Exception {
        logger.info("Attempting to join a game as an observer.");
        String endpoint = "/game";
        URL url = new URL(SERVER_BASE_URL + endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("PUT");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");
        if (this.authToken != null) {
            connection.setRequestProperty("Authorization", this.authToken);
        }

        String jsonRequestBody = gson.toJson(Map.of("gameID", gameId));

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
            logger.info("Joined game successfully as observer: Game ID " + gameId);
            return true;
        } else {
            logger.warning("Failed to join game as observer: " + responseCode);
            throw new Exception("Failed to join game as observer: HTTP error code " + responseCode);
        }
    }
    public String getAuthToken() {
        return this.authToken;
    }


    public void setServerBaseUrl(String baseUrl) {
        SERVER_BASE_URL = baseUrl;
    }

}

