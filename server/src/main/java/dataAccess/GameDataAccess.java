package dataAccess;

import model.GameData;
import java.util.List;

public interface GameDataAccess {
    void insertGame(GameData game) throws DataAccessException;
    GameData getGame(Integer gameID) throws DataAccessException;
    void updateGame(GameData game) throws DataAccessException;
//    void deleteGame(Integer gameID) throws DataAccessException;
    List<GameData> listGames() throws DataAccessException;
    void clearGames() throws DataAccessException;

}
