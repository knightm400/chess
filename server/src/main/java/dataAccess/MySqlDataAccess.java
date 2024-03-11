package dataAccess;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import model.*;
import dataAccess.*;

import javax.xml.crypto.Data;

public class MySqlDataAccess implements AuthDataAccess, GameDataAccess, UserDataAccess {
    private static final Gson gson = new Gson();

    @Override
    public void insertAuth(AuthData auth) throws DataAccessException {}

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {}

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {}

    @Override
    public List<AuthData> listAuths() throws DataAccessException {
        // SQL to list all auth tokens
    }

    // Implementing methods from GameDataAccess
    @Override
    public void insertGame(GameData game) throws DataAccessException {
        // Example SQL: "INSERT INTO game_table (gameID, whiteUsername, ...) VALUES (?, ?, ...)"
        // Convert complex objects to JSON string if necessary using gson
    }

    @Override
    public GameData getGame(Integer gameID) throws DataAccessException {
        // SQL to get a game by ID
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        // SQL to update a game's details
    }

    @Override
    public void deleteGame(Integer gameID) throws DataAccessException {
        // SQL to delete a game
    }

    @Override
    public List<GameData> listGames() throws DataAccessException {
        // SQL to list all games
    }

    // Implementing methods from UserDataAccess
    @Override
    public void insertUser(UserData user) throws DataAccessException {
        // SQL to insert a new user
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        // SQL to get a user by username
    }

    @Override
    public void updateUser(UserData user) throws DataAccessException {
        // SQL to update user's details
    }

    @Override
    public void deleteUser(String username) throws DataAccessException {
        // SQL to delete a user
    }

    @Override
    public List<UserData> listUsers() throws DataAccessException {
        // SQL to list all users
    }

    @Override
    public UserData validateUser(String username, String password) throws DataAccessException {
        // SQL to validate a user's login
    }

    // Additional private helper methods as needed, similar to your Memory-based classes
}
