package dataAccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MySqlDataAccess implements AuthDataAccess, GameDataAccess, UserDataAccess {

    public MySqlDataAccess() {
    }

    @Override
    public void insertAuth(AuthData auth) throws DataAccessException {
        String sql = "INSERT INTO AuthTokens (authToken, username) VALUES (?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setString(1, auth.authToken());
            preparedStatement.setString(2, auth.username());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error inserting auth token: " + e.getMessage());
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        String sql = "SELECT authToken, username FROM AuthTokens WHERE authToken = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setString(1, authToken);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    String retrievedToken = rs.getString("authToken");
                    String username = rs.getString("username");
                    return new AuthData(retrievedToken, username);
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error retrieving auth token: " + e.getMessage());
        }
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        String sql = "DELETE FROM AuthTokens WHERE authToken = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setString(1, authToken);
            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new DataAccessException("Auth token deletion failed: No such token found.");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting auth token: " + e.getMessage());
        }
    }

    @Override
    public List<AuthData> listAuths() throws DataAccessException {
        List<AuthData> auths = new ArrayList<>();
        String sql = "SELECT authToken, username FROM AuthTokens";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql);
             ResultSet rs = preparedStatement.executeQuery()) {
            while (rs.next()) {
                String authToken = rs.getString("authToken");
                String username = rs.getString("username");
                auths.add(new AuthData(authToken, username));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error listing auth tokens: " + e.getMessage());
        }
        return auths;
    }

    @Override
    public void clearAuths() throws DataAccessException {
        String sql = "TRUNCATE TABLE AuthTokens";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error clearing auth tokens: " + e.getMessage());
        }
    }

    @Override
    public String generateAuthToken() {
        // Generates a random UUID as an auth token.
        return UUID.randomUUID().toString();
    }

    @Override
    public AuthData getAuthByUsername(String username) throws DataAccessException {
        String sql = "SELECT authToken, username FROM AuthTokens WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setString(1, username);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    String authToken = rs.getString("authToken");
                    return new AuthData(authToken, username);
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error retrieving auth by username: " + e.getMessage());
        }
    }

    @Override
    public void insertUser(UserData user) throws DataAccessException {
        String sql = "INSERT INTO Users (username, password, email) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setString(1, user.username());
            preparedStatement.setString(2, user.password());
            preparedStatement.setString(3, user.email());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error inserting user: " + e.getMessage());
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        String sql = "SELECT username, password, email FROM Users WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setString(1, username);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    return new UserData(rs.getString("username"), rs.getString("password"), rs.getString("email"));
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error retrieving user: " + e.getMessage());
        }
    }

    @Override
    public void updateUser(UserData user) throws DataAccessException {
        String sql = "UPDATE Users SET password = ?, email = ? WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setString(1, user.password());
            preparedStatement.setString(2, user.email());
            preparedStatement.setString(3, user.username());
            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new DataAccessException("User update failed: User not found.");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error updating user: " + e.getMessage());
        }
    }

    @Override
    public void deleteUser(String username) throws DataAccessException {
        String sql = "DELETE FROM Users WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setString(1, username);
            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new DataAccessException("User deletion failed: User not found.");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting user: " + e.getMessage());
        }
    }

    @Override
    public List<UserData> listUsers() throws DataAccessException {
        List<UserData> userList = new ArrayList<>();
        String sql = "SELECT username, password, email FROM Users";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql);
             ResultSet rs = preparedStatement.executeQuery()) {
            while (rs.next()) {
                userList.add(new UserData(rs.getString("username"), rs.getString("password"), rs.getString("email")));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error listing users: " + e.getMessage());
        }
        return userList;
    }

    @Override
    public UserData validateUser(String username, String password) throws DataAccessException {
        UserData user = getUser(username);
        if (user != null && user.password().equals(password)) {
            return user;
        } else {
            throw new DataAccessException("Invalid username or password.");
        }
    }

    @Override
    public void clearUsers() throws DataAccessException {
        String sql = "TRUNCATE TABLE Users";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error clearing users: " + e.getMessage());
        }
    }
}

}