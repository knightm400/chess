package server;

import service.*;
import dataAccess.*;
public class Main {
    public static void main(String[] args) {
        AuthService authService = new AuthService(new UserDataAccess());
        ClearService clearService = new ClearService(new UserDataAccess(), new GameDataAccess());
        CreateGameService createGameService = new CreateGameService(new GameDataAccess());
        ListGamesService listGamesService = new ListGamesService(new GameDataAccess());
        LoginService loginService = new LoginService(new UserDataAccess());
        LogoutService logoutService = new LogoutService(new AuthDataAccess());
        RegisterService registerService = new RegisterService(new UserDataAccess());
        JoinGameService joinGameService = new JoinGameService(new GameDataAccess());
        Server server = new Server(authService, clearService, createGameService, listGamesService, loginService, logoutService, registerService, joinGameService);
        server.run(8080);
    }
}
