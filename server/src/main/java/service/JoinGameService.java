package service;

import dataAccess.GameDataAccess;
import dataAccess.AuthDataAccess;
import dataAccess.DataAccessException;
import model.AuthData;
import model.GameData;
import service.Result.JoinGameResult;

public class JoinGameService {

    private final GameDataAccess gameDataAccess;
    private final AuthDataAccess authDataAccess;

    public JoinGameService(GameDataAccess gameDataAccess, AuthDataAccess authDataAccess) {
        this.gameDataAccess = gameDataAccess;
        this.authDataAccess = authDataAccess;
    }

    public JoinGameResult joinGame(String authToken, Integer gameID, String playerColor) throws DataAccessException {
        AuthData authData = authDataAccess.getAuth(authToken);
        if (authData == null) {
            throw new DataAccessException("Unauthorized");
        }

        GameData gameData = gameDataAccess.getGame(gameID);
        if (gameData == null) {
            throw new DataAccessException("Bad Request");
        }

        if ("WHITE".equals(playerColor) && gameData.whiteUsername() != null) {
            throw new DataAccessException("Already taken");
        } else if ("BLACK".equals(playerColor) && gameData.blackUsername() != null) {
            throw new DataAccessException("Already taken");
        }

        if ("WHITE".equals(playerColor)) {
            gameData = new GameData(
                    gameID,
                    authData.username(),
                    gameData.blackUsername(),
                    gameData.gameName(),
                    gameData.gameData(),
                    "WHITE",
                    gameData.blackColor()
            );
        } else if ("BLACK".equals(playerColor)) {
            gameData = new GameData(
                    gameID,
                    gameData.whiteUsername(),
                    authData.username(),
                    gameData.gameName(),
                    gameData.gameData(),
                    gameData.whiteColor(),
                    "BLACK"
            );
        }

        gameDataAccess.updateGame(gameData);

        return new JoinGameResult(gameID, playerColor, true, "Successfully joined the game");
    }
}
