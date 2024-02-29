package dataAccess;

import model.GameData;
import java.util.List;

public interface IGameDataAccess {
    void createGame(GameData game) throws DataAccessException;
    GameData getGame(String gameID) throws DataAccessException;
    List<GameData> listGames() throws DataAccessException;
    void updateGame(String gameID, GameData game) throws DataAccessException;
}
