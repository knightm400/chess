package dataAccess;

import model.GameData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameDataAccess {
    private final Map<String, GameData> games = new HashMap<>();

    public void clearDatabase() {
        games.clear();
    }

    public void insertGame(GameData game) {
        games.put(game.getGameID(), game);
    }

    public GameData getGame(String gameID) {
        return games.get(gameID);
    }

    public void updateGame(String gameID, GameData game) {
        games.put(gameID, game);
    }

    public void deleteGame(String gameID) {
        games.remove(gameID);
    }

    public List<GameData> getAllGames() {
        return new ArrayList<>(games.values());
    }
}
