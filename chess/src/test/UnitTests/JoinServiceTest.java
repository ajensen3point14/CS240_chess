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
import requests.JoinRequest;
import server.services.ClearService;
import server.services.JoinService;

import static org.junit.jupiter.api.Assertions.*;

class JoinServiceTest {
    @BeforeEach
    public void testSetup() {
        ClearService clear = new ClearService();
        clear.clear();
    }

    @Test
    @DisplayName("Junit join game success")
    public void successJoin() {
        // Create a user, authToken, and a game
        User user = new User("Doug", "Fillmore", "d@f.com");
        UserDAO.getInstance().insert(user);
        AuthToken token = AuthTokenDAO.getInstance().create("Doug");
        Game game = new Game(0, "myGame");
        GameDAO.getInstance().addGame(game);

        assertNotNull(UserDAO.getInstance().find("Doug", "Fillmore"));
        assertNotNull(AuthTokenDAO.getInstance().find(token.getAuthToken()));

        // Add the user to the game as the WHITE player
        JoinRequest request = new JoinRequest();
        request.setAuthToken(token.getAuthToken());
        request.setPlayerColor("WHITE");
        request.setGameID(game.getGameID());
        JoinService join = new JoinService();
        join.join(request);

        assertEquals("Doug", GameDAO.getInstance().find(request.getGameID()).getWhiteUsername());

        // Add the user to the game as the BLACK player
        request = new JoinRequest();
        request.setAuthToken(token.getAuthToken());
        request.setPlayerColor("BLACK");
        request.setGameID(game.getGameID());
        join = new JoinService();
        join.join(request);

        assertEquals("Doug", GameDAO.getInstance().find(request.getGameID()).getWhiteUsername());

        // Add the user to the game as an observer
        request = new JoinRequest();
        request.setAuthToken(token.getAuthToken());
        request.setPlayerColor("");
        request.setGameID(game.getGameID());
        join = new JoinService();
        join.join(request);

        assertEquals(1, GameDAO.getInstance().find(request.getGameID()).getObservers().size());
    }

    @Test
    @DisplayName("Junit join game fail")
    public void failJoin() {
        // Create a user, authToken, and a game
        User user = new User("Doug", "Fillmore", "d@f.com");
        UserDAO.getInstance().insert(user);
        AuthToken token = AuthTokenDAO.getInstance().create("Doug");
        Game game = new Game(0, "myGame");
        GameDAO.getInstance().addGame(game);

        assertNotNull(UserDAO.getInstance().find("Doug", "Fillmore"));
        assertNotNull(AuthTokenDAO.getInstance().find(token.getAuthToken()));

        // Try to add the player to the PINK team
        JoinRequest request = new JoinRequest();
        request.setAuthToken(token.getAuthToken());
        request.setPlayerColor("PINK");
        request.setGameID(game.getGameID());
        JoinService join = new JoinService();
        assertThrows(MyServerException.class, () -> join.join(request));
    }

}