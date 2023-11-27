package UnitTests;

import dataAccess.DataAccessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import server.DAO.UserDAO;
import models.User;
import requests.RegisterRequest;
import results.RegisterResult;
import server.services.ClearService;
import server.services.RegisterService;

import static org.junit.jupiter.api.Assertions.*;

class UserDAOTest {
    @BeforeEach
    public void clearDB() {
        ClearService clear = new ClearService();
        clear.clear();
    }

    @Test
    @DisplayName("Junit clear users")
    public void clearUsers() {
        User newUser = new User("Doug", "Fillmore", "A@B.com");
        UserDAO.getInstance().insert(newUser);
        assertEquals("Doug", UserDAO.getInstance().find("Doug", "Fillmore").getUsername());

        UserDAO.getInstance().clear();
        assertNull(UserDAO.getInstance().find("Doug", "Fillmore"));
    }

    @Test
    @DisplayName("Junit insert success")
    public void successInsert() {
        RegisterRequest request = new RegisterRequest();
        RegisterService register = new RegisterService();
        new RegisterResult();

        request.setUsername("Doug");
        request.setPassword("Fillmore");
        request.setEmail("a@b.com");

        User newUser = new User(request.getUsername(), request.getPassword(), request.getEmail());
        UserDAO.getInstance().insert(newUser);

        assertEquals("Doug", UserDAO.getInstance().find(request.getUsername(), request.getPassword()).getUsername());
    }

    @Test
    @DisplayName("Junit insert failure")
    public void failInsert() {
        RegisterRequest request = new RegisterRequest();
        RegisterService register = new RegisterService();
        new RegisterResult();

        request.setUsername("Doug");
        request.setPassword("Fillmore");
        request.setEmail("a@b.com");

        RegisterResult result = register.register(request);

        assertEquals("Doug", UserDAO.getInstance().find(request.getUsername(), request.getPassword()).getUsername());

        // Insert another user with the same username, which should fail
        RegisterRequest newReq = new RegisterRequest();
        new RegisterResult();

        newReq.setUsername("Doug");
        newReq.setPassword("Fillmore");
        newReq.setEmail("a@b.com");

        User newUser = new User(newReq.getUsername(), newReq.getPassword(), newReq.getEmail());

        assertThrows(DataAccessException.class, () -> UserDAO.getInstance().insert(newUser));
    }

    @Test
    @DisplayName("Junit find success")
    public void successFind() {
        // Create new user, and insert into DB
        RegisterRequest request = new RegisterRequest();
        RegisterService register = new RegisterService();
        new RegisterResult();

        request.setUsername("Doug");
        request.setPassword("Fillmore");
        request.setEmail("a@b.com");
        register.register(request);

        // find user
        assertEquals("Doug", UserDAO.getInstance().find(request.getUsername(), request.getPassword()).getUsername());
    }

    @Test
    @DisplayName("Junit find fail")
    public void failFind() {
        assertNull(UserDAO.getInstance().find("Doug", "Fillmore"));
    }
}