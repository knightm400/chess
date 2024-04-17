package webSocketMessages.userCommands;

public class LeaveCommand extends UserGameCommand {
    private int gameId;
    private String username;

    public LeaveCommand(String authToken, int gameId) {
        super(authToken);
        this.gameId = gameId;
        this.username = username;
        this.commandType = CommandType.LEAVE;
    }

    public int getGameId() {
        return gameId;
    }

    public String getUsername() {
        return username;
    }
}
