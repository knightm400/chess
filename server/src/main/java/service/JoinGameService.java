package service;

import dataAccess.GameDataAccess;
import dataAccess.AuthDataAccess;
import dataAccess.DataAccessException;
import model.AuthData;
import model.GameData;
import service.JoinGameResult;
import java.util.HashSet;
import java.util.Set;

public class JoinGameService {

    private final GameDataAccess gameDataAccess;
    private final AuthDataAccess authDataAccess;

    public JoinGameService(GameDataAccess gameDataAccess, AuthDataAccess authDataAccess) {
        this.gameDataAccess = gameDataAccess;
        this.authDataAccess = authDataAccess;
    }

    public JoinGameResult joinGame(String authToken, String gameID, String playerColor) throws DataAccessException {
        AuthData authData = authDataAccess.getAuth(authToken);
        if (authData == null) {
            throw new DataAccessException("Invalid or expired authToken.");
        }

        GameData gameData = gameDataAccess.getGame(gameID);
        if (gameData == null) {
            throw new DataAccessException("Game does not exist.");
        }

        Set<String> takenColors = new HashSet<>(gameData.takenColors());
        String assignedColor = playerColor;
        if (playerColor != null && !takenColors.contains(playerColor)) {
            takenColors.add(playerColor);
            gameData = new GameData(gameID, "WHITE".equals(playerColor) ? authData.username() : gameData.whiteUsername(),
                    "BLACK".equals(playerColor) ? authData.username() : gameData.blackUsername(),
                    gameData.gameName(), gameData.gameData(),
                    "WHITE".equals(playerColor) ? playerColor : gameData.whiteColor(),
                    "BLACK".equals(playerColor) ? playerColor : gameData.blackColor(),
                    takenColors);
            gameDataAccess.updateGame(gameData);
        } else {
            assignedColor = "OBSERVER";
        }

        return new JoinGameResult(gameID, assignedColor, true, "Successfully joined the game");
    }
}
