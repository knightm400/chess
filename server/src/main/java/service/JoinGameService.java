package service;

import dataAccess.IUserDataAccess;
import dataAccess.DataAccessException;
import model.GameData;
import model.UserData;

public class JoinGameService {
    private IUserDataAccess userDataAccess;
    public JoinGameService(IUserDataAccess userDataAccess) {
        this.userDataAccess = userDataAccess;
    }

    public JoinGameResult joinGame(JoinGameRequest request) {
        try {
            String authToken = request.getAuthToken();
            UserData userData = userDataAccess.getUser(authToken);

            if (userData == null) {
                return new JoinGameResult(false, "Unauthorized");
            }
            String username = userData.getUsername();
            String gameID = request.getGameID();
            GameData game = userDataAccess.getGame(gameID);
            if (game == null) {
                return new JoinGameResult(false, "Game not found");
            }

            String clientColor = request.getClientColor();
            if (clientColor != null && !game.isColorAvailable(clientColor)) {
                return new JoinGameResult(false, "Color already taken");
            }

            game.setPlayerColor(username, clientColor);
            userDataAccess.updateGame(gameID, game);

            return new JoinGameResult(true, "Joined game successfully");
        } catch (DataAccessException e) {
            return new JoinGameResult(false, "Error: " + e.getMessage());
        }
    }
}
