package dataAccess;

import model.GameData;
import java.util.List;

public interface IGameDataAccess {
    void insertGame(GameData game) throws DataAccessException;
    GameData getGame(String gameName) throws DataAccessException;
    void deleteGame(String gameName) throws DataAccessException;
    List<GameData> getAllGames() throws DataAccessException;
}
