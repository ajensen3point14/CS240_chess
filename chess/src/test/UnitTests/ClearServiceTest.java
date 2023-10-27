package UnitTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import server.DAO.AuthTokenDAO;
import server.DAO.GameDAO;
import server.DAO.UserDAO;
import server.MyServerException;
import server.models.AuthToken;
import server.requests.AuthTokenRequest;
import server.results.ListResult;
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
        UserDAO.getInstance().insert("Doug", "Fillmore", "d@f.com");
        AuthToken token = AuthTokenDAO.getInstance().create("Doug");
        GameDAO.getInstance().addGame("myGame");

        assertNotNull(UserDAO.getInstance().find("Doug", "Fillmore"));
        assertNotNull(AuthTokenDAO.getInstance().find("Doug"));
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
        assertThrows(MyServerException.class, () -> UserDAO.getInstance().find("Doug", "Fillmore"));
        assertThrows(MyServerException.class, () -> AuthTokenDAO.getInstance().find("Doug"));
        assertThrows(MyServerException.class, () -> AuthTokenDAO.getInstance().find(token.getAuthToken()));
    }
}