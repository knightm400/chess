package dataAccess;

import model.GameData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class GameDataAccess implements IGameDataAccess {
    private Map<String, GameData> games = new HashMap<>();

    @Override
    public void insertGame(GameData game) throws DataAccessException {
        games.put(game.getGameName(), game);
    }

    @Override
    public GameData getGame(String gameName) throws DataAccessException {
        return games.get(gameName);
    }

    @Override
    public void deleteGame(String gameName) throws DataAccessException {
        games.remove(gameName);
    }

    @Override
    public List<GameData> getAllGames() throws DataAccessException {
        return new ArrayList<>(games.values());
    }
}
