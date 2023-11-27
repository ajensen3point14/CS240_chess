package UnitTests;

import dataAccess.DataAccessException;
import dataAccess.Database;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import server.DAO.GameDAO;
import models.Game;
import server.services.ClearService;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

class GameDAOTest {
    @BeforeEach
    public void clearDB() {
        ClearService clear = new ClearService();
        clear.clear();
    }

    @Test
    @DisplayName("Junit clear games")
    public void clearGames() {
        // create a game in the DB and find it
        Game game = new Game(1, "myGame");
        assertEquals(game, GameDAO.getInstance().addGame(game));
        assertEquals(game.getGameName(), GameDAO.getInstance().find(game.getGameID()).getGameName());

        GameDAO.getInstance().clear();
        assertNull(GameDAO.getInstance().find(game.getGameID()));
    }

    @Test
    @DisplayName("Junit create and find success")
    public void successCreateFind() {
        // create a game in the DB and find it
        Game game = new Game(1, "myGame");
        assertEquals(game, GameDAO.getInstance().addGame(game));
        assertEquals(game.getGameName(), GameDAO.getInstance().find(game.getGameID()).getGameName());
    }

    @Test
    @DisplayName("Junit create and find failure")
    public void failCreateFind() {
        Game game = new Game(1, "123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890");
        assertThrows(DataAccessException.class, () -> GameDAO.getInstance().addGame(game));
        Game newGame = new Game(1, null);
        assertThrows(DataAccessException.class, () -> GameDAO.getInstance().addGame(newGame));
        assertNull(GameDAO.getInstance().find(newGame.getGameID()));
    }

    @Test
    @DisplayName("Junit findAll success")
    public void successFindAll() {
        Game game1 = new Game(1, "game1");
        assertEquals(game1, GameDAO.getInstance().addGame(game1));
        Game game2 = new Game(2, "game2");
        assertEquals(game2, GameDAO.getInstance().addGame(game2));

        assertEquals(2, GameDAO.getInstance().findAll().size());

        GameDAO.getInstance().clear();
        assertEquals(0, GameDAO.getInstance().findAll().size());
    }

    @Test
    @DisplayName("Junit findAll fail")
    public void failFindAll() {
        Game game1 = new Game(1, "game1");
        assertEquals(game1, GameDAO.getInstance().addGame(game1));
        Game game2 = new Game(2, "game2");
        assertEquals(game2, GameDAO.getInstance().addGame(game2));

        try (Connection connection = Database.connection();
             Statement statement = connection.createStatement()) {
            String dropTableSQL = "DROP TABLE IF EXISTS game";
            statement.execute(dropTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        assertThrows(DataAccessException.class, () -> GameDAO.getInstance().findAll());

        // Clean up by recreating the game table
        String createTableSQL = "CREATE TABLE IF NOT EXISTS game (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "game_name VARCHAR(255) NOT NULL," +
                "white_username VARCHAR(255)," +
                "black_username VARCHAR(255)," +
                "observers VARCHAR(1024)," +
                "game_state VARCHAR(1024)" +
                ")";
        try (Connection connection = Database.connection();
             Statement statement = connection.createStatement()) {
            statement.execute(createTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error creating game table");
        }
    }

    @Test
    @DisplayName("Junit update success")
    public void successUpdate() {
        Game game = new Game(1, "myGame");
        assertEquals(game, GameDAO.getInstance().addGame(game));

        game.setWhiteUsername("Doug");
        game.setBlackUsername("Phil");
        GameDAO.getInstance().update(game);

        assertEquals("Doug", GameDAO.getInstance().find(game.getGameID()).getWhiteUsername());
        assertEquals("Phil", GameDAO.getInstance().find(game.getGameID()).getBlackUsername());
    }

    @Test
    @DisplayName("Junit update fail")
    public void failUpdate() {
        Game game = new Game(1, "myGame");
        assertEquals(game, GameDAO.getInstance().addGame(game));

        game.setGameID(-1);
        assertThrows(DataAccessException.class, () -> GameDAO.getInstance().update(game));
    }
}