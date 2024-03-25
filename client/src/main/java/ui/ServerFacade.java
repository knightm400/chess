package ui;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import model.MessageResponse;
import service.Result.LoginResult;
import service.Result.RegisterResult;

public class ServerFacade {
    private static final String SERVER_BASE_URL = "http://localhost:8080";
    private static final Gson gson = new Gson();

    public String register(String username, String password, String email) throws Exception {
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
            return result.authToken();
        } else {
            throw new Exception("Registration failed with status: " + responseCode);
        }
    }

    public String login(String username, String password) throws Exception {
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
            LoginResult result = gson.fromJson(response.toString(), LoginResult.class);
            return result.authToken();
        } else if (responseCode == 401) {
            throw new Exception("Login failed: unauthorized (wrong username or password)");
        } else {
            throw new Exception("Login failed with status: " + responseCode);
        }
    }

    public String logout(String authToken) throws Exception {
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
            return "Logged out successfully.";
        } else {
            MessageResponse messageResponse = gson.fromJson(response.toString(), MessageResponse.class);
            throw new Exception("Logout failed: " + messageResponse.getMessage());
        }
    }


}

