package ClientUnitTesting;

import clientUI.MyClientException;
import clientUI.ServerFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClientUnitTests {
    @BeforeEach
    public void clear() {
        ServerFacade server = new ServerFacade();
        server.clear();
    }

    @Test
    @DisplayName("Test successful registration")
    void registerPos() {
        ServerFacade server = new ServerFacade();

        assertNull(server.getToken());

        String username = "Doug";
        String password = "fillmore";
        String email = "a@b.com";

        server.register(username, password, email);

        assertNotNull(server.getToken());
    }

    @Test
    @DisplayName("Test register twice")
    void registerNeg() {
        ServerFacade server = new ServerFacade();

        assertNull(server.getToken());

        String username = "Doug";
        String password = "fillmore";
        String email = "a@b.com";

        server.register(username, password, email);

        assertNotNull(server.getToken());

        assertThrows(MyClientException.class, () -> server.register(username, password,email));
    }

    @Test
    @DisplayName("Test successful login")
    void loginPos() {
        // Register a new user
        ServerFacade server = new ServerFacade();

        assertNull(server.getToken());

        String username = "Doug";
        String password = "fillmore";
        String email = "a@b.com";

        server.register(username, password, email);
        String registerToken = server.getToken();

        assertNotNull(server.getToken());

        // Log user in
        server.login(username, password);

        String loginToken = server.getToken();
        assertNotNull(server.getToken());
        assertNotEquals(registerToken, loginToken);

        assertTrue(server.getLoggedIn());
    }

    @Test
    @DisplayName("Login invalid user")
    void loginNeg() {
        // Ensure no user has been logged in/registered
        ServerFacade server = new ServerFacade();

        assertNull(server.getToken());

        String username = "Doug";
        String password = "fillmore";

        assertThrows(MyClientException.class, () -> server.login(username, password));
    }

    @Test
    @DisplayName("Test successful logout")
    void logoutPos() {
        // Register a new user
        ServerFacade server = new ServerFacade();

        assertNull(server.getToken());

        String username = "Doug";
        String password = "fillmore";
        String email = "a@b.com";

        server.register(username, password, email);
        String registerToken = server.getToken();

        assertNotNull(server.getToken());

        // Log user in
        server.login(username, password);

        String loginToken = server.getToken();
        assertNotNull(server.getToken());
        assertNotEquals(registerToken, loginToken);

        assertTrue(server.getLoggedIn());

        // Log user out
        server.logout();

        assertFalse(server.getLoggedIn());
    }

    @Test
    @DisplayName("Logout invalid user")
    void logoutNeg() {
        // Ensure no user has been logged in/registered
        ServerFacade server = new ServerFacade();

        assertNull(server.getToken());

        assertThrows(MyClientException.class, () -> server.logout());
    }

    @Test
    @DisplayName("Create a new game")
    void createPos() {
        // Register a new user
        ServerFacade server = new ServerFacade();

        String username = "Doug";
        String password = "fillmore";
        String email = "a@b.com";

        server.register(username, password, email);

        // Log user in
        server.login(username, password);

        String gameName = "game1";

        server.create(gameName);

        assertNotNull(server.getCurrGameID());
    }

    @Test
    @DisplayName("Create an invalid game")
    void createNeg() {
        // Try to create a game without being logged in
        ServerFacade server = new ServerFacade();

        String gameName = "game1";

        assertThrows(MyClientException.class, () -> server.create(gameName));
    }

    @Test
    @DisplayName("Successfully list all games")
    void listPos() {
        // Register a new user
        ServerFacade server = new ServerFacade();

        String username = "Doug";
        String password = "fillmore";
        String email = "a@b.com";

        server.register(username, password, email);

        // Log user in
        server.login(username, password);

        String gameName = "game1";
        String game2Name = "game2";

        server.create(gameName);
        server.create(game2Name);

        server.list();

        assertEquals(server.getGamesList().getGames().size(), 2);
    }

    @Test
    @DisplayName("Create an invalid game")
    void listNeg() {
        // Register a new user
        ServerFacade server = new ServerFacade();

        String username = "Doug";
        String password = "fillmore";
        String email = "a@b.com";

        server.register(username, password, email);

        // Log user in
        server.login(username, password);

        // Create a game
        String gameName = "game1";
        server.create(gameName);

        // Log user out
        server.logout();

        assertThrows(MyClientException.class, () -> server.list());
    }

    @Test
    @DisplayName("Successfully join a game")
    void joinPos() {
        // Register a user, log them in, and create a game
        ServerFacade server = new ServerFacade();

        String username = "Doug";
        String password = "fillmore";
        String email = "a@b.com";

        server.register(username, password, email);

        // Log user in
        server.login(username, password);

        // Create a game
        String gameName = "game1";
        server.create(gameName);
        server.list();

        // Join as WHITE player
        server.join(1, "WHITE");
        server.list();
        assertEquals("Doug", server.getGamesList().getGames().get(server.getCurrGameID() - 1).getWhiteUsername());
    }

    @Test
    @DisplayName("Join an invalid game")
    void joinNeg() {
        // Register a user, log them in, and create a game
        ServerFacade server = new ServerFacade();

        String username = "Doug";
        String password = "fillmore";
        String email = "a@b.com";

        server.register(username, password, email);

        // Log user in
        server.login(username, password);

        // Create a game
        String gameName = "game1";
        server.create(gameName);
        server.list();

        // Join invalid game 2 as WHITE player
        assertThrows(MyClientException.class, () -> server.join(2, "WHITE"));
    }

    @Test
    @DisplayName("Observe a valid game")
    void observePos() {
        // Register a user, log them in, and create a game
        ServerFacade server = new ServerFacade();

        String username = "Doug";
        String password = "fillmore";
        String email = "a@b.com";

        server.register(username, password, email);

        // Log user in
        server.login(username, password);

        // Create a game
        String gameName = "game1";
        server.create(gameName);
        server.list();

        // Join game as observer
        server.join(1, null);
        server.list();
        assertNull(server.getGamesList().getGames().get(server.getCurrGameID() - 1).getWhiteUsername());
        assertNull(server.getGamesList().getGames().get(server.getCurrGameID() - 1).getBlackUsername());
    }

    @Test
    @DisplayName("Observe an invalid game")
    void observeNeg() {
        // Register a user, log them in, and create a game
        ServerFacade server = new ServerFacade();

        String username = "Doug";
        String password = "fillmore";
        String email = "a@b.com";

        server.register(username, password, email);

        // Log user in
        server.login(username, password);

        // Create a game
        String gameName = "game1";
        server.create(gameName);
        server.list();

        // Join invalid game2 as observer
        assertThrows(MyClientException.class, () -> server.join(2, null));
    }
}