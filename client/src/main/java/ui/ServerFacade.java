package ui;

import dataAccess.AuthDataAccess;
import dataAccess.GameDataAccess;
import dataAccess.UserDataAccess;
import dataAccess.DataAccessException;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.List;

public class ServerFacade {
    private final UserDataAccess userDataAccess;
    private final GameDataAccess gameDataAccess;
    private final AuthDataAccess authDataAccess;

    public ServerFacade(UserDataAccess userDataAccess, GameDataAccess gameDataAccess, AuthDataAccess authDataAccess) {
        this.userDataAccess = userDataAccess;
        this.gameDataAccess = gameDataAccess;
        this.authDataAccess = authDataAccess;
    }

    public UserData register(String username, String password, String email) throws DataAccessException {
        return null;
    }

    public UserData login(String username, String password) throws DataAccessException {
        return null;
    }

    public void logout(String authToken) throws DataAccessException {
    }

    public List<GameData> listGames(String authToken) throws DataAccessException {
        return null;
    }

    public GameData createGame(String authToken, String gameName) throws DataAccessException {
        return null;
    }

    public GameData joinGame(String authToken, Integer gameID) throws DataAccessException {
        return null;
    }

}

