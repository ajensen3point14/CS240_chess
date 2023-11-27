package UnitTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import server.DAO.AuthTokenDAO;
import server.DAO.GameDAO;
import server.DAO.UserDAO;
import server.MyServerException;
import models.AuthToken;
import models.Game;
import models.User;
import requests.AuthTokenRequest;
import results.ListResult;
import server.services.ClearService;
import server.services.ListService;

import static org.junit.jupiter.api.Assertions.*;

class ClearServiceTest {
    @BeforeEach
    public void testSetup() {
        ClearService clear = new ClearService();
        clear.clear();
    }

    @Test
    @DisplayName("Junit clear")
    public void successClear() {
        // Create a user, authToken, and a game
        User user = new User("Doug", "Fillmore", "d@f.com");
        UserDAO.getInstance().insert(user);
        AuthToken token = AuthTokenDAO.getInstance().create("Doug");
        Game game = new Game(0, "myGame");
        GameDAO.getInstance().addGame(game);

        assertNotNull(UserDAO.getInstance().find("Doug", "Fillmore"));
        assertNotNull(AuthTokenDAO.getInstance().find(token.getAuthToken()));

        // Verify that the database has been populated
        ListService listService = new ListService();
        AuthTokenRequest request = new AuthTokenRequest();
        request.setAuthToken(token.getAuthToken());
        ListResult result = listService.list(request);

        assertNotNull(result);
        assertEquals(1, result.getGames().size());

        // call clear
        ClearService clear = new ClearService();
        clear.clear();

        // Verify that all databases have been cleared
        assertThrows(MyServerException.class, () -> listService.list(request));
        assertEquals(0, GameDAO.getInstance().findAll().size());
        assertNull(UserDAO.getInstance().find("Doug", "Fillmore"));
        assertNull(AuthTokenDAO.getInstance().find(token.getAuthToken()));
    }
}