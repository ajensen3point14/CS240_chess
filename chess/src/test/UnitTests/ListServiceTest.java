package UnitTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import server.DAO.AuthTokenDAO;
import server.DAO.GameDAO;
import server.DAO.UserDAO;
import server.MyServerException;
import server.models.AuthToken;
import server.models.Game;
import server.models.User;
import server.requests.AuthTokenRequest;
import server.results.ListResult;
import server.services.ClearService;
import server.services.ListService;

import static org.junit.jupiter.api.Assertions.*;

class ListServiceTest {
    @BeforeEach
    public void testSetup() {
        ClearService clear = new ClearService();
        clear.clear();
    }

    @Test
    @DisplayName("Junit list game success")
    public void successList() {
        // Create a user, authToken, and a game
        User user = new User("Doug", "Fillmore", "d@f.com");
        UserDAO.getInstance().insert(user);
        AuthToken token = AuthTokenDAO.getInstance().create("Doug");
        Game game = new Game(0, "myGame");
        GameDAO.getInstance().addGame(game);

        assertNotNull(UserDAO.getInstance().find("Doug", "Fillmore"));
        assertNotNull(AuthTokenDAO.getInstance().find(token.getAuthToken()));

        // Verify that the database has been populated with 1 game
        ListService listService = new ListService();
        AuthTokenRequest request = new AuthTokenRequest();
        request.setAuthToken(token.getAuthToken());
        ListResult result = listService.list(request);

        assertNotNull(result);
        assertEquals(1, result.getGames().size());

        // Add another game
        Game game2 = new Game(1, "myGame2");
        GameDAO.getInstance().addGame(game2);

        // Verify that the database has been populated with 2 games
        result = listService.list(request);

        assertNotNull(result);
        assertEquals(2, result.getGames().size());

        // List the games
        assertEquals(2, GameDAO.getInstance().findAll().size());
    }

    @Test
    @DisplayName("Junit list game fail")
    public void failList() {
        // Create a user, authToken, and a game
        User user = new User("Doug", "Fillmore", "d@f.com");
        UserDAO.getInstance().insert(user);

        // Set a bad authToken, which is required to call "list"
        AuthTokenRequest request = new AuthTokenRequest();
        request.setAuthToken("");

        // Make a call to list the games with a bad authToken
        ListService list = new ListService();
        assertThrows(MyServerException.class, () -> list.list(request));
    }

}