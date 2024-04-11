package webSocketMessages.userCommands;

public class ResignCommand extends UserGameCommand {
    private int gameId;

    public ResignCommand(String authToken, int gameId) {
        super(authToken);
        this.gameId = gameId;
        this.commandType = CommandType.RESIGN;
    }

    public int getGameId() {
        return gameId;
    }
}