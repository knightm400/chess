package dataAccess;

import java.sql.*;
import java.util.Properties;

public class DatabaseManager {
    private static final String databaseName;
    private static final String user;
    private static final String password;
    private static final String baseConnectionUrl;

    /*
     * Load the database information from the db.properties file.
     */
    static {
        try {
            try (var propStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties")) {
                if (propStream == null) throw new Exception("Unable to load db.properties");
                Properties props = new Properties();
                props.load(propStream);
                databaseName = props.getProperty("db.name");
                user = props.getProperty("db.user");
                password = props.getProperty("db.password");

                var host = props.getProperty("db.host");
                var port = Integer.parseInt(props.getProperty("db.port"));
                baseConnectionUrl = String.format("jdbc:mysql://%s:%d", host, port);
            }
        } catch (Exception ex) {
            throw new RuntimeException("Unable to process db.properties: " + ex.getMessage());
        }
    }

    public static void initializeDatabaseAndTables() throws SQLException {
        try (Connection conn = DriverManager.getConnection(baseConnectionUrl, user, password);
             Statement statement = conn.createStatement()) {
            String createDatabase = "CREATE DATABASE IF NOT EXISTS " + databaseName;
            statement.execute(createDatabase);

            try (Connection dbConn = DriverManager.getConnection(baseConnectionUrl + "/" + databaseName, user, password);
                 Statement dbStatement = dbConn.createStatement()) {
                String createUsersTable = "CREATE TABLE IF NOT EXISTS Users (" +
                        "username VARCHAR(255) NOT NULL PRIMARY KEY, " +
                        "password VARCHAR(255) NOT NULL, " +
                        "email VARCHAR(255) NOT NULL);";
                String createAuthTokensTable = "CREATE TABLE IF NOT EXISTS AuthTokens (" +
                        "authToken VARCHAR(255) NOT NULL PRIMARY KEY, " +
                        "username VARCHAR(255) NOT NULL, " +
                        "FOREIGN KEY (username) REFERENCES Users(username));";
                String createGamesTable = "CREATE TABLE IF NOT EXISTS Games (" +
                        "gameID INT AUTO_INCREMENT PRIMARY KEY, " +
                        "whiteUsername VARCHAR(255) NOT NULL, " +
                        "blackUsername VARCHAR(255) NOT NULL, " +
                        "gameName VARCHAR(255), " +
                        "gameData TEXT, " +
                        "whiteColor VARCHAR(10), " +
                        "blackColor VARCHAR(10), " +
                        "FOREIGN KEY (whiteUsername) REFERENCES Users(username), " +
                        "FOREIGN KEY (blackUsername) REFERENCES Users(username));";
                dbStatement.execute(createUsersTable);
                dbStatement.execute(createAuthTokensTable);
                dbStatement.execute(createGamesTable);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error initializing database and tables: " + e.getMessage());
        }
    }

    /**
     * Create a connection to the database and sets the catalog based upon the
     * properties specified in db.properties. Connections to the database should
     * be short-lived, and you must close the connection when you are done with it.
     * The easiest way to do that is with a try-with-resource block.
     * <br/>
     * <code>
     * try (var conn = DbInfo.getConnection(databaseName)) {
     * // execute SQL statements.
     * }
     * </code>
     */

    static Connection getConnection() throws SQLException {
        try {
            var conn = DriverManager.getConnection(baseConnectionUrl + "/" + databaseName, user, password);
            return conn;
        } catch (SQLException e) {
            throw new RuntimeException("Error connecting to the database: " + e.getMessage());
        }
    }
}
