package server.DAO;

import dataAccess.DataAccessException;
import dataAccess.Database;
import models.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Interactions between the users and the database will occur here.
 * For now, the database is just a hashMap
 */
public class UserDAO implements DAO{
    private final Connection connection;

    // Singleton pattern for users
    private static UserDAO single_instance = null;

    public static synchronized UserDAO getInstance() {
        if (single_instance == null)
            single_instance = new UserDAO();

        return single_instance;
    }

    private UserDAO() {
        this.connection = Database.connection();
        // Create DB tables
        String createTableSQL = "CREATE TABLE IF NOT EXISTS user (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "name VARCHAR(255) NOT NULL UNIQUE," +
                "password VARCHAR(255) NOT NULL," +
                "email VARCHAR(255)" +
                ")";
        try (Statement statement = connection.createStatement()) {
            statement.execute(createTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error creating user table");
        }
    }

    /**
     * Insert a user into the database.
     */
    public void insert(User user) {
        String insertSQL = "INSERT INTO user (name, password, email) VALUES (?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setString(3, user.getEmail());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error inserting user");
        }
    }

    /**
     * Find a user by username.
     */
    public User find(String username, String password) {
        String query = "SELECT * FROM user WHERE name = ? AND password = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setString(2, password);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    // User found, create and return the User object
                    User foundUser = new User(resultSet.getString("name"), resultSet.getString("password"), resultSet.getString("email"));
                    return foundUser;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Clear the user database.
     */
    @Override
    public void clear() {
        String clearTableSQL = "TRUNCATE TABLE user";
        try (Statement statement = connection.createStatement()) {
            statement.execute(clearTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
