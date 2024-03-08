package service;

import dataAccess.GameDataAccess;
import dataAccess.AuthDataAccess;
import dataAccess.DataAccessException;
import model.AuthData;
import model.GameData;
import service.*;

import java.util.HashSet;
import java.util.Set;
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
        Integer gameID = UUID.randomUUID().hashCode();
        String gameName = request.gameName();
        String whiteUsername = username;
        String blackUsername = "";
        Set<String> takenColors = new HashSet<>();
        takenColors.add("WHITE");

        GameData newGame = new GameData(gameID, whiteUsername, blackUsername, gameName, "", "WHITE", "", takenColors);
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

        Set<String> updatedColors = new HashSet<>(gameData.takenColors());
        if (request.playerColor() != null && !updatedColors.contains(request.playerColor())) {
            updatedColors.add(request.playerColor());

            String whiteUsername = gameData.whiteUsername();
            String blackUsername = gameData.blackUsername();
            if ("WHITE".equals(request.playerColor())) {
                whiteUsername = authData.username();
            } else if ("BLACK".equals(request.playerColor())) {
                blackUsername = authData.username();
            }

            gameDataAccess.updateGame(new GameData(
                    gameData.gameID(),
                    whiteUsername,
                    blackUsername,
                    gameData.gameName(),
                    gameData.gameData(),
                    gameData.whiteColor(),
                    gameData.blackColor(),
                    updatedColors
            ));
            return new JoinGameResult(request.gameID(), request.playerColor(), true, "Joined game successfully.");
        } else {
            return new JoinGameResult(request.gameID(), null, true, "Joined as observer.");
        }
    }
}