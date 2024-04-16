package webSocketMessages.userCommands;

import chess.ChessGame;
import com.google.gson.annotations.SerializedName;

public class JoinPlayerCommand extends UserGameCommand {
    @SerializedName("gameID")
    private int gameId;
    private ChessGame.TeamColor playerColor;

    public JoinPlayerCommand(String authToken, int gameId, ChessGame.TeamColor playerColor) {
        super(authToken);
        this.commandType = CommandType.JOIN_PLAYER;
        this.gameId = gameId;
        this.playerColor = playerColor;
    }

    public int getGameId() {
        return gameId;
    }
}
