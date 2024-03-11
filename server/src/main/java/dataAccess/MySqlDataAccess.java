package dataAccess;

import model.AuthData;
import model.GameData;
import model.UserData;
import java.util.List;

public class MySqlDataAccess implements AuthDataAccess, GameDataAccess, UserDataAccess {

    // Constructor
    public MySqlDataAccess() {
    }

    // AuthDataAccess implementation
    @Override
    public void insertAuth(AuthData auth) {
        // Implement database insert logic
    }

    @Override
    public AuthData getAuth(String authToken) {
        // Implement fetch logic
        return null; // Placeholder return
    }

    @Override
    public void deleteAuth(String authToken) {
        // Implement fetch logic
    }

    @Override
    public List<AuthData> listAuths() {
        return null;
    }

    @Override
    public void clearAuths() {
    }

    @Override
    public String generateAuthToken() {
        return null;
    }

    @Override
    public AuthData getAuthByUsername(String username) {
        return null;
    }

    // GameDataAccess implementation
    @Override
    public void insertGame(GameData game) {
        // Implement database insert logic
    }

    @Override
    public GameData getGame(Integer gameID) {
        // Implement fetch logic
        return null; // Placeholder return
    }

    @Override
    public void updateGame(GameData game) {

    }

    @Override
    public void deleteGame(Integer gameID) {}
    public List<GameData> listGames() {
        return null;
    }
    public void clearGames() {}


    // UserDataAccess implementation
    @Override
    public void insertUser(UserData user) {
        // Implement database insert logic
    }

    @Override
    public UserData getUser(String username) {
        // Implement fetch logic
        return null; // Placeholder return
    }

    @Override
    public void updateUser(UserData user) {
    }
    public void deleteUser(String username) {}
    public List<UserData> listUsers() {
        return null;
    }
    public UserData validateUser(String username, String password) {
        return null;
    }
    public void clearUsers() {}


}

