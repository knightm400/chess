import chess.*;
import ui.PreLogin;
import ui.ServerFacade;

public class Main {
    public static void main(String[] args) {
        System.out.println("♕ 240 Chess Client: Starting...");
        ServerFacade serverFacade = new ServerFacade();
        PreLogin preLogin = new PreLogin(serverFacade);
        preLogin.displayMenu();
    }
}

