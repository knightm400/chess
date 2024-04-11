package webSocketMessages.userCommands;

public class JoinObserverCommand extends UserGameCommand {
    private int gameId;

    public JoinObserverCommand(String authToken, int gameId) {
        super(authToken);
        this.gameId = gameId;
        this.commandType = CommandType.JOIN_OBSERVER;
    }

    public int getGameId() {
        return gameId;
    }
}
