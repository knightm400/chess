package webSocketMessages.userCommands;

public class ResignCommand extends UserGameCommand {
    private int gameId;
    private String username;

    public ResignCommand(String authToken, int gameId, String username) {
        super(authToken);
        this.gameId = gameId;
        this.username = username;
        this.commandType = CommandType.RESIGN;
    }

    public int getGameId() {
        return gameId;
    }

    public String getUsername() {
        return username;
    }
}
