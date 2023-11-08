package UnitTests;

import dataAccess.DataAccessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import server.DAO.AuthTokenDAO;
import server.models.AuthToken;
import server.services.ClearService;

import static org.junit.jupiter.api.Assertions.*;

class AuthTokenDAOTest {
    @BeforeEach
    public void clearDB() {
        ClearService clear = new ClearService();
        clear.clear();
    }

    @Test
    @DisplayName("Junit create and find success")
    public void successCreateFind() {
        // create a token in the DB and find it
        AuthToken token = AuthTokenDAO.getInstance().create("Doug");
        assertEquals(token.getAuthToken(), AuthTokenDAO.getInstance().find(token.getAuthToken()).getAuthToken());
    }

    @Test
    @DisplayName("Junit create failure")
    public void failCreate() {
        assertThrows(DataAccessException.class, () -> AuthTokenDAO.getInstance().create(null));
        assertThrows(DataAccessException.class, () -> AuthTokenDAO.getInstance().create("123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890"));
    }

    @Test
    @DisplayName("Junit find fail")
    public void failFind() {
        AuthToken bogus = new AuthToken("Doug", "Fillmore");
        assertNull(AuthTokenDAO.getInstance().find(bogus.getAuthToken()));
    }

    @Test
    @DisplayName("Junit remove success")
    public void successRemove() {
        AuthToken token = AuthTokenDAO.getInstance().create("Doug");
        assertEquals(token.getAuthToken(), AuthTokenDAO.getInstance().find(token.getAuthToken()).getAuthToken());

        AuthTokenDAO.getInstance().remove(token.getAuthToken());
        assertNull(AuthTokenDAO.getInstance().find(token.getAuthToken()));
    }

    @Test
    @DisplayName("Junit remove fail")
    public void failRemove() {
        AuthToken bogus = new AuthToken("Doug", "Fillmore");
        assertThrows(DataAccessException.class, () -> AuthTokenDAO.getInstance().remove(bogus.getAuthToken()));
    }

}