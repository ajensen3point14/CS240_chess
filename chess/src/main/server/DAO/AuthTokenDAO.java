package server.DAO;

import dataAccess.DataAccessException;
import dataAccess.Database;
import models.AuthToken;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

/**
 * Interactions between AuthTokens and the database are stored here.
 * AuthTokens are stored in a hashmap in two ways: once as the token to the username, and
 * the other as a username to the token, providing order one lookup in either direction.
 */
public class AuthTokenDAO implements DAO{
    private final Connection connection;

    private static AuthTokenDAO single_instance = null;

    public static synchronized AuthTokenDAO getInstance() {
        if (single_instance == null)
            single_instance = new AuthTokenDAO();

        return single_instance;
    }

    private AuthTokenDAO() {
        this.connection = Database.connection();
        // Create DB tables
        String createTableSQL = "CREATE TABLE IF NOT EXISTS auth_token (" +
                "token VARCHAR(255) PRIMARY KEY," +
                "username VARCHAR(255) NOT NULL" +
                ")";
        try (Statement statement = connection.createStatement()) {
            statement.execute(createTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error creating auth_token table");
        }
    }

    /**
     * Creates a new authToken for the given username.
     */
    public AuthToken create(String username) {
        // generate unique random token
        String token = UUID.randomUUID().toString();
        String insertSQL = "INSERT INTO auth_token (token, username) VALUES (?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
            preparedStatement.setString(1, token);
            preparedStatement.setString(2, username);
            preparedStatement.executeUpdate();

            return new AuthToken(token, username);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error creating an auth token");
        }
    }

    /**
     * Find the authToken for the associated username.
     */
    public AuthToken find(String myToken) {
        String findSQL = "SELECT * FROM auth_token WHERE token = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(findSQL)) {
            preparedStatement.setString(1, myToken);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String username = resultSet.getString("username");
                return new AuthToken(myToken, username);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Remove the token for the specified username.
     */
    public void remove(String token) {
        String removeSQL = "DELETE FROM auth_token WHERE token = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(removeSQL)) {
            preparedStatement.setString(1, token);
            int deletedRows = preparedStatement.executeUpdate();

            if (deletedRows == 0) {
                throw new DataAccessException("Auth token not found");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Clear the auth_token database.
     */
    @Override
    public void clear() {
        String clearTableSQL = "TRUNCATE TABLE auth_token";
        try (Statement statement = connection.createStatement()) {
            statement.execute(clearTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
