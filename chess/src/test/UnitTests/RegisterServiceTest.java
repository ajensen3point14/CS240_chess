package UnitTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import server.DAO.UserDAO;
import server.MyServerException;
import server.requests.RegisterRequest;
import server.results.RegisterResult;
import server.services.ClearService;
import server.services.RegisterService;

import static org.junit.jupiter.api.Assertions.*;

class RegisterServiceTest {
    @BeforeEach
    public void testSetup() {
        ClearService clear = new ClearService();
        clear.clear();
    }

    @Test
    @DisplayName("Junit register success")
    public void successRegister() {
        RegisterRequest request = new RegisterRequest();
        RegisterService register = new RegisterService();
        new RegisterResult();

        request.setUsername("Doug");
        request.setPassword("Fillmore");
        request.setEmail("a@b.com");

        RegisterResult result = register.register(request);

        assertEquals("Doug", UserDAO.getInstance().find(request.getUsername(), request.getPassword()).getUsername());
    }

    @Test
    @DisplayName("Junit register fail")
    public void failRegister() {
        RegisterRequest request = new RegisterRequest();
        RegisterService register = new RegisterService();
        new RegisterResult();

        // Set a bad register name
        request.setUsername("");
        request.setPassword("Fillmore");
        request.setEmail("a@b.com");

        assertThrows(MyServerException.class, () -> register.register(request));
    }

}