package ui;

import model.GameData;
import service.Result.JoinGameResult;

import java.util.List;
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
        try {
            String message = serverFacade.logout();
            logger.info("User logged out successfully in PostLogin: " + message);
            System.out.println("You have been logged out. Returning to PreLogin screen...");
            PreLogin preLogin = new PreLogin(serverFacade);
            preLogin.displayMenu();
        } catch (Exception e) {
            logger.severe("Logout failed in PostLogin: " + e.getMessage());
            System.out.println("Logout failed: " + e.getMessage());
        }
    }

    private void createGame(Scanner scanner) {
        logger.info("Prompting user for new game details in PostLogin.");
        System.out.print("Enter name for new game: ");
        String gameName = scanner.nextLine().trim();

        try {
            GameData newGame = serverFacade.createGame(serverFacade.getAuthToken(), gameName);
            if (newGame != null) {
                logger.info("New game created successfully in PostLogin: " + gameName);
                System.out.println("Game '" + gameName + "' created successfully!");
            } else {
                logger.warning("New game creation failed for unknown reasons in PostLogin.");
                System.out.println("Failed to create the game due to an unknown error.");
            }
        } catch (Exception e) {
            logger.severe("New game creation failed in PostLogin: " + e.getMessage());
            System.out.println("Failed to create the game: " + e.getMessage());
        }
    }

    private void listGames() {
        logger.info("Listing all games in PostLogin.");
        try {
            List<GameData> games = serverFacade.listGames();
            if (games.isEmpty()) {
                System.out.println("There are currently no games available.");
            } else {
                System.out.println("Available games:");
                int gameNumber = 1;
                for (GameData game : games) {
                    System.out.println(gameNumber++ + ". " + game.gameName() + " (ID: " + game.gameID() + ")");
                }
            }
        } catch (Exception e) {
            logger.severe("Failed to list games in PostLogin: " + e.getMessage());
            System.out.println("Failed to retrieve the list of games: " + e.getMessage());
        }
    }

    private void joinGame(Scanner scanner) {
        logger.info("Prompting user for game ID and player color in PostLogin.");

        listGames();

        System.out.print("Enter the ID of the game you want to join: ");
        Integer gameId = null;
        while (true) {
            String input = scanner.nextLine().trim();
            try {
                gameId = Integer.parseInt(input);
                break;
            } catch (NumberFormatException e) {
                System.out.print("Invalid game ID. Please enter a valid integer: ");
            }
        }

        System.out.print("Enter the color you want to play as (white/black): ");
        String playerColor = scanner.nextLine().trim().toLowerCase();
        while (!playerColor.equals("white") && !playerColor.equals("black")) {
            System.out.print("Invalid color. Please enter 'white' or 'black': ");
            playerColor = scanner.nextLine().trim().toLowerCase();
        }

        try {
            JoinGameResult result = serverFacade.joinGame(serverFacade.getAuthToken(), gameId, playerColor);
            if (result != null && result.success()) {
                logger.info("Successfully joined game " + gameId + " as " + playerColor + " in PostLogin.");
                System.out.println("Successfully joined game " + gameId + " as " + playerColor + ".");
            } else {
                logger.warning("Failed to join game in PostLogin.");
                System.out.println("Failed to join the game. Please try again.");
            }
        } catch (Exception e) {
            logger.severe("Joining a game failed in PostLogin: " + e.getMessage());
            System.out.println("Failed to join the game: " + e.getMessage());
        }
    }

    private void joinObserver(Scanner scanner) {
        logger.info("Joining a game as an observer in PostLogin.");
        listGames();

        System.out.print("Enter the ID of the game you want to observe: ");
        int gameId;
        while (true) {
            String input = scanner.nextLine().trim();
            try {
                gameId = Integer.parseInt(input);
                break;
            } catch (NumberFormatException e) {
                System.out.print("Invalid game ID. Please enter a valid number: ");
            }
        }

        try {
            boolean success = serverFacade.joinGameAsObserver(gameId);
            if (success) {
                logger.info("Successfully joined game " + gameId + " as observer.");
                System.out.println("Successfully joined game " + gameId + " as an observer.");
            } else {
                logger.warning("Failed to join game as observer.");
                System.out.println("Failed to join the game as an observer. Please try again.");
            }
        } catch (Exception e) {
            logger.severe("Joining a game as an observer failed: " + e.getMessage());
            System.out.println("Failed to join the game as an observer: " + e.getMessage());
        }
    }
}
