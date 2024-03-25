package ui;

import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class PreLogin {
    private final ServerFacade serverFacade;
    private static final Logger logger = Logger.getLogger(PreLogin.class.getName());

    static {
        try {
            FileHandler fileHandler = new FileHandler("PreLogin.log", true);
            logger.addHandler(fileHandler);
        } catch (Exception e) {
            logger.severe("Failed to initialize log file handler.");
        }
    }


    public PreLogin(ServerFacade serverFacade) {
        this.serverFacade = serverFacade;
    }

    public void displayMenu() {
        logger.info("Displaying PreLogin menu.");
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\nPreLogin Menu:");
            System.out.println("1. Help");
            System.out.println("2. Quit");
            System.out.println("3. Login");
            System.out.println("4. Register");
            System.out.print("Enter choice: ");

            String choice = scanner.nextLine();
            logger.info("User choice: " + choice);

            switch (choice) {
                case "1":
                    logger.info("Displaying help information.");
                    displayHelp();
                    break;
                case "2":
                    logger.info("User chose to quit the application.");
                    running = false;
                    break;
                case "3":
                    logger.info("User chose to login.");
                    login(scanner);
                    break;
                case "4":
                    logger.info("User chose to register.");
                    register(scanner);
                    break;
                default:
                    logger.warning("Invalid option chosen: " + choice);
                    System.out.println("Invalid option. Please try again.");
            }
        }
        scanner.close();
        logger.info("PreLogin menu closed.");
    }

    private void displayHelp() {
        logger.info("Providing help information to the user.");
        System.out.println("Help Information:");
        System.out.println("- Type '1' for help.");
        System.out.println("- Type '2' to quit the application.");
        System.out.println("- Type '3' to login.");
        System.out.println("- Type '4' to register a new account.");
    }

    private void login(Scanner scanner) {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        try {
            String authToken = serverFacade.login(username, password);
            logger.info("Login successful for user: " + username);
            System.out.println("Login successful. Transitioning to PostLogin...");
            PostLogin postLogin = new PostLogin(serverFacade);
            postLogin.displayMenu();
            return;
        } catch (Exception e) {
            logger.severe("Login failed for user: " + username + "; Reason: " + e.getMessage());
            System.out.println("Login failed: " + e.getMessage());
        }

        if (authToken != null) {
            serverFacade.setAuthToken(authToken);
            logger.info("Login successful for user: " + username);
            System.out.println("Login successful. Transitioning to PostLogin...");
            PostLogin postLogin = new PostLogin(serverFacade);
            postLogin.displayMenu();
        }
    }

    private void register(Scanner scanner) {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();

        logger.info("Attempting to register new user: " + username);
        try {
            String authToken = serverFacade.register(username, password, email);
            if (authToken != null) {
                logger.info("Registration successful for user: " + username);
                System.out.println("Registration successful. Transitioning to PostLogin...");
                PostLogin postLogin = new PostLogin(serverFacade);
                postLogin.displayMenu();
                return;
            }
        } catch (Exception e) {
            logger.severe("Registration failed for user: " + username + "; Reason: " + e.getMessage());
            System.out.println("Registration failed: " + e.getMessage());
        }
    }

    if (authToken != null) {
        serverFacade.setAuthToken(authToken);
        logger.info("Registration successful for user: " + username);
        System.out.println("Registration successful. Transitioning to PostLogin...");
        PostLogin postLogin = new PostLogin(serverFacade);
        postLogin.displayMenu();
    }
}
