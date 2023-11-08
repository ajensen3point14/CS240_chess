package server.DAO;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dataAccess.DataAccessException;
import dataAccess.Database;
import server.models.Game;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Interactions between the database and the list of games.
 * These are stored for now as a HashMap, but this will change in phase 4.
 * The maximum number of supported games is defined here.
 */
public class GameDAO implements DAO{
    private final Connection connection;
    protected Gson gson = new Gson();

    private static GameDAO single_instance = null;

    public static synchronized GameDAO getInstance() {
        if (single_instance == null)
            single_instance = new GameDAO();

        return single_instance;
    }

    private GameDAO() {
        this.connection = Database.connection();
        // Create DB table, if it doesn't already exist
        String createTableSQL = "CREATE TABLE IF NOT EXISTS game (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "game_name VARCHAR(255) NOT NULL," +
                "white_username VARCHAR(255)," +
                "black_username VARCHAR(255)," +
                "observers VARCHAR(1024)," +
                "game_state VARCHAR(1024)" +
                ")";
        try (Statement statement = connection.createStatement()) {
            statement.execute(createTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error creating game table");
        }
    }


    /**
     * Add a game to the database. Throw a max capacity error if full.
     * This will set a gameID, and set the given name for the game.
     */
    public Game addGame(Game game) {
        String insertSQL = "INSERT INTO game (game_name) VALUES (?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, game.getGameName());
            preparedStatement.executeUpdate();

            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                int gameId = generatedKeys.getInt(1);
                game.setGameID(gameId);
            } else {
                throw new DataAccessException("Failed to create game");
            }
            return game;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error adding a game");
        }
    }

    /**
     * Find a specific game by its ID.
     */
    public Game find(int id) {
        String findSQL = "SELECT * FROM game WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(findSQL)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return extractGame(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Find all games in the database.
     */
    public Collection<Game> findAll() {
        String findAllSQL = "SELECT * FROM game";
        List<Game> games = new ArrayList<>();
        try (Statement statement = connection.prepareStatement(findAllSQL)) {
            ResultSet resultSet = statement.executeQuery(findAllSQL);
            while (resultSet.next()) {
                Game game = extractGame(resultSet);
                games.add(game);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("FindAll error");
        }
        return games;
    }

    // helper function for find and findAll methods
    private Game extractGame(ResultSet resultSet) throws SQLException {
        Game game = new Game(
                resultSet.getInt("id"),
                resultSet.getString("game_name")
        );
        game.setWhiteUsername(resultSet.getString("white_username"));
        game.setBlackUsername(resultSet.getString("black_username"));
        ArrayList<String> obsList = gson.fromJson(resultSet.getString("observers"),
                new TypeToken<List<String>>(){}.getType());
        if (obsList == null) { obsList = new ArrayList<>(); }
        game.setObservers(obsList);

        return game;
    }

    public void update(Game game) {
        // Construct SQL statements for each column update
        String updateGameSQL = "UPDATE game SET game_name = ?,  " +
                "white_username = ?, " +
                "black_username = ?, " +
                "observers = ?, " +
                "game_state = ? " +
                "WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateGameSQL)) {
            preparedStatement.setString(1, game.getGameName());
            preparedStatement.setString(2, game.getWhiteUsername());
            preparedStatement.setString(3, game.getBlackUsername());
            String observerStr = gson.toJson(game.getObservers());
            preparedStatement.setString(4, observerStr);
            preparedStatement.setString(5, ""); // TODO: update the game state
            preparedStatement.setInt(6, game.getGameID());
            int updatedRows = preparedStatement.executeUpdate();

            if (updatedRows == 0) {
                throw new DataAccessException("Game not found or not updated");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error updating a game");
        }
    }

    /**
     * Clear the game database.
     */
    @Override
    public void clear() {
        String clearTableSQL = "TRUNCATE TABLE game";
        try (Statement statement = connection.createStatement()) {
            statement.execute(clearTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
