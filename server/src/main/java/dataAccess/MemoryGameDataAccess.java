package dataAccess;

import model.GameData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemoryGameDataAccess implements GameDataAccess {
    private final Map<String, GameData> games = new HashMap<>();

    @Override
    public void insertGame(GameData game) throws DataAccessException {
        if (games.containsKey(game.gameID())) {
            throw new DataAccessException("Game already exists.");
        }
        games.put(game.gameID(), game);
    }

    @Override
    public GameData getGame(String gameID) throws DataAccessException {
        if (!games.containsKey(gameID)) {
            throw new DataAccessException("Game does not exist.");
        }
        return games.get(gameID);
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        if (!games.containsKey(game.gameID())) {
            throw new DataAccessException("Game does not exist.");
        }
        games.put(game.gameID(), game);
    }

    @Override
    public void deleteGame(String gameID) throws DataAccessException {
        if (!games.containsKey(gameID)) {
            throw new DataAccessException("Game does not exist.");
        }
        games.remove(gameID);
    }

    @Override
    public List<GameData> listGames() {
        return new ArrayList<>(games.values());
    }

    @Override
    public void clearGames() {
        games.clear();
    }
}
