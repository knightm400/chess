package ui;

import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class PostLogin {
    private final ServerFacade serverFacade;
    private static final Logger logger = Logger.getLogger(PostLogin.class.getName());

    static {
        try {
            FileHandler fileHandler = new FileHandler("PostLogin.log", true);
            logger.addHandler(fileHandler);
        } catch (Exception e) {
            logger.severe("Failed to initialize log file handler for PostLogin: " + e.getMessage());
        }
    }

    public PostLogin(ServerFacade serverFacade) {
        this.serverFacade = serverFacade;
    }

    public void displayMenu() {
        logger.info("Displaying PostLogin menu.");
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\nPostLogin Menu:");
            System.out.println("1. Help");
            System.out.println("2. Logout");
            System.out.println("3. Create Game");
            System.out.println("4. List Games");
            System.out.println("5. Join Game");
            System.out.println("6. Join Observer");
            System.out.print("Enter choice: ");

            String choice = scanner.nextLine();
            logger.info("User choice in PostLogin: " + choice);

            switch (choice) {
                case "1":
                    logger.info("Displaying help information in PostLogin.");
                    displayHelp();
                    break;
                case "2":
                    logger.info("User chose to logout in PostLogin.");
                    logout();
                    running = false; // Assuming the transition to PreLogin will happen after logout
                    break;
                case "3":
                    logger.info("User chose to create a game in PostLogin.");
                    createGame(scanner);
                    break;
                case "4":
                    logger.info("User chose to list games in PostLogin.");
                    listGames();
                    break;
                case "5":
                    logger.info("User chose to join a game in PostLogin.");
                    joinGame(scanner);
                    break;
                case "6":
                    logger.info("User chose to join as an observer in PostLogin.");
                    joinObserver(scanner);
                    break;
                default:
                    logger.warning("Invalid option chosen in PostLogin: " + choice);
                    System.out.println("Invalid option. Please try again.");
            }
        }
        scanner.close();
    }

    private void displayHelp() {
        logger.info("Providing help information to the user in PostLogin.");
        System.out.println("Help Information for PostLogin:");
        System.out.println("- Type '1' for help. This will display all available commands you can perform.");
        System.out.println("- Type '2' to logout. This will log you out of the system and bring you back to the PreLogin screen.");
        System.out.println("- Type '3' to create a game. This allows you to create a new game which other players can join.");
        System.out.println("- Type '4' to list all available games. You will see a list of all games that currently exist.");
        System.out.println("- Type '5' to join a game. This allows you to join an existing game as a player.");
        System.out.println("- Type '6' to join as an observer. This allows you to join an existing game as an observer, meaning you can watch but not participate in the game.");
    }

    private void logout() {
        logger.info("Attempting to log out user in PostLogin.");
        String authoken = 
        try {
            serverFacade.logout(); // Update this line if your logout method requires parameters.
            logger.info("User logged out successfully in PostLogin.");
            System.out.println("You have been logged out. Returning to PreLogin screen...");
            PreLogin preLogin = new PreLogin(serverFacade);
            preLogin.displayMenu();
        } catch (Exception e) {
            logger.severe("Logout failed in PostLogin: " + e.getMessage());
            System.out.println("Logout failed: " + e.getMessage());
        }
    }

    private void createGame(Scanner scanner) {
        // Code to create a new game
        logger.info("Creating a new game in PostLogin.");
    }

    private void listGames() {
        // Code to list all existing games
        logger.info("Listing all games in PostLogin.");
    }

    private void joinGame(Scanner scanner) {
        // Code to join a game
        logger.info("Joining a game in PostLogin.");
    }

    private void joinObserver(Scanner scanner) {
        // Code to join a game as an observer
        logger.info("Joining a game as an observer in PostLogin.");
    }
}
