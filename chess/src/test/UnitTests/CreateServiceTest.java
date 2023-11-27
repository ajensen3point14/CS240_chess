package UnitTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import server.DAO.AuthTokenDAO;
import server.DAO.GameDAO;
import server.DAO.UserDAO;
import server.MyServerException;
import models.AuthToken;
import models.User;
import requests.CreateRequest;
import server.services.ClearService;
import server.services.CreateService;

import static org.junit.jupiter.api.Assertions.*;

class CreateServiceTest {
    @BeforeEach
    public void testSetup() {
        ClearService clear = new ClearService();
        clear.clear();
    }

    @Test
    @DisplayName("Junit Create game success")
    public void successCreate() {
        // Create a user and an authToken (there must be a user to create a game)
        User user = new User("Doug", "Fillmore", "d@f.com");
        UserDAO.getInstance().insert(user);
        AuthToken token = AuthTokenDAO.getInstance().create("Doug");

        assertNotNull(UserDAO.getInstance().find("Doug", "Fillmore"));
        assertNotNull(AuthTokenDAO.getInstance().find(token.getAuthToken()));

        // Add a game
        CreateRequest request = new CreateRequest();
        request.setGameName("myGame");
        request.setAuthToken(token.getAuthToken());
        CreateService createService = new CreateService();
        createService.create(request);

        // Verify that the database has been populated
        assertEquals(1, GameDAO.getInstance().findAll().size());
    }

    @Test
    @DisplayName("Junit create game negative")
    public void failCreate() {
        // Create a user and an authToken (there must be a user to create a game)
        User user = new User("Doug", "Fillmore", "d@f.com");
        UserDAO.getInstance().insert(user);
        AuthToken token = AuthTokenDAO.getInstance().create("Doug");

        assertNotNull(UserDAO.getInstance().find("Doug", "Fillmore"));
        assertNotNull(AuthTokenDAO.getInstance().find(token.getAuthToken()));

        // Try to add a game with no name
        CreateRequest request = new CreateRequest();
        request.setGameName("");
        request.setAuthToken(token.getAuthToken());
        CreateService createService = new CreateService();

        assertThrows(MyServerException.class, () -> createService.create(request));
    }
}