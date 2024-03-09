// CreateGameService.java
package service;

import dataAccess.AuthDataAccess;
import dataAccess.DataAccessException;
import dataAccess.GameDataAccess;
import model.AuthData;
import model.GameData;
import service.CreateGameRequest;
import service.CreateGameResult;

import java.util.HashSet;
import java.util.UUID;

public class CreateGameService {

    private final AuthDataAccess authDataAccess;
    private final GameDataAccess gameDataAccess;

    public CreateGameService(AuthDataAccess authDataAccess, GameDataAccess gameDataAccess) {
        this.authDataAccess = authDataAccess;
        this.gameDataAccess = gameDataAccess;
    }

    public CreateGameResult createGame(CreateGameRequest request) throws DataAccessException {
        AuthData authData = authDataAccess.getAuth(request.authToken());
        if (authData == null) {
            throw new DataAccessException("Unauthorized");
        }

        if (request.gameName() == null) {
            throw new DataAccessException("Bad Request");
        }

        Integer gameID = Math.abs(UUID.randomUUID().hashCode());
        if (gameID < 0) {
            gameID = Math.abs(gameID+ 1);
        }

        GameData newGame = new GameData(gameID, null, null, request.gameName(), "", null, null);
        gameDataAccess.insertGame(newGame);

        return new CreateGameResult(newGame.gameID());
    }
}
