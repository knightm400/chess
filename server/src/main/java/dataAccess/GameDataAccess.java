package dataAccess;

import model.GameData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class GameDataAccess implements IGameDataAccess {
    private Map<String, GameData> games = new HashMap<>();

    @Override
    public void createGame(GameData game) throws DataAccessException {
        games.put(game.getGameName(), game);
    }

    @Override
    public GameData getGame(String gameID) throws DataAccessException {
        return games.get(gameID);
    }

    @Override
    public List<GameData> listGames() throws DataAccessException {
        return new ArrayList<>(games.values());

    }

    @Override
    public void updateGame(String gameID, GameData game) throws DataAccessException {
        games.put(gameID, game);
    }
}
