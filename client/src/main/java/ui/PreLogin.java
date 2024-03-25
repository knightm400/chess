package ui;

import java.util.Scanner;

public class PreLogin {
    private final ServerFacade serverFacade;

    public PreLogin(ServerFacade serverFacade) {
        this.serverFacade = serverFacade;
    }

    public void displayMenu() {
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

            switch (choice) {
                case "1":
                    displayHelp();
                    break;
                case "2":
                    running = false;
                    break;
                case "3":
                    login(scanner);
                    break;
                case "4":
                    register(scanner);
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
        scanner.close();
    }

    private void displayHelp() {
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
            System.out.println("Login successful.");
        } catch (Exception e) {
            System.out.println("Login failed: " + e.getMessage());
        }
    }

    private void register(Scanner scanner) {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();

        try {
            String authToken = serverFacade.register(username, password, email);
            System.out.println("Registration successful.");
        } catch (Exception e) {
            System.out.println("Registration failed: " + e.getMessage());
        }
    }
}
