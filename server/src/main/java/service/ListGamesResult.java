package service;

import java.util.List;
import model.GameData;

public class ListGamesResult {
    private final List<GameData> games;
    private final boolean success;
    private final String message;

    public ListGamesResult(List<GameData> games, boolean success, String message) {
        this.games = games;
        this.success = success;
        this.message = message;
    }

}
