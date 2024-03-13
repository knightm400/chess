package service;

import dataAccess.GameDataAccess;
import dataAccess.AuthDataAccess;
import dataAccess.DataAccessException;
import model.AuthData;
import model.GameData;
import service.Request.CreateGameRequest;
import service.Request.JoinGameRequest;
import service.Result.CreateGameResult;
import service.Result.JoinGameResult;

import java.util.UUID;

public class GameService {

    private GameDataAccess gameDataAccess;
    private AuthDataAccess authDataAccess;

    public GameService(GameDataAccess gameDataAccess, AuthDataAccess authDataAccess) {
        this.gameDataAccess = gameDataAccess;
        this.authDataAccess = authDataAccess;
    }

    public CreateGameResult createGame(CreateGameRequest request) throws DataAccessException {
        String authToken = request.authToken();
        AuthData authData = authDataAccess.getAuth(authToken);
        if (authData == null) {
            throw new DataAccessException("Invalid or expired authToken.");
        }

        String username = authData.username();
        Integer gameID = Math.abs(UUID.randomUUID().hashCode());
        if (gameID < 0) {
            gameID = Math.abs(gameID + 1);
        }
        String gameName = request.gameName();

        GameData newGame = new GameData(gameID, username, "", gameName, "", "WHITE", "");
        gameDataAccess.insertGame(newGame);

        return new CreateGameResult(gameID);
    }

    public JoinGameResult joinGame(JoinGameRequest request) throws DataAccessException {
        AuthData authData = authDataAccess.getAuth(request.authToken());
        if (authData == null) {
            throw new DataAccessException("Invalid or expired authToken.");
        }

        GameData gameData = gameDataAccess.getGame(request.gameID());
        if (gameData == null) {
            return new JoinGameResult(request.gameID(), request.playerColor(), false, "Game does not exist.");
        }

        if ("WHITE".equals(request.playerColor()) && gameData.whiteUsername() != null) {
            return new JoinGameResult(request.gameID(), null, false, "White color already taken.");
        } else if ("BLACK".equals(request.playerColor()) && gameData.blackUsername() != null) {
            return new JoinGameResult(request.gameID(), null, false, "Black color already taken.");
        }

        if ("WHITE".equals(request.playerColor())) {
            gameData = new GameData(
                    gameData.gameID(),
                    authData.username(),
                    gameData.blackUsername(),
                    gameData.gameName(),
                    gameData.gameData(),
                    "WHITE",
                    gameData.blackColor()
            );
        } else if ("BLACK".equals(request.playerColor())) {
            gameData = new GameData(
                    gameData.gameID(),
                    gameData.whiteUsername(),
                    authData.username(),
                    gameData.gameName(),
                    gameData.gameData(),
                    gameData.whiteColor(),
                    "BLACK"
            );
        }

        gameDataAccess.updateGame(gameData);

        return new JoinGameResult(request.gameID(), request.playerColor(), true, "Joined game successfully.");
    }
}
