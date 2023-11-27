package commands;

import clientUI.ServerFacade;
import models.AuthToken;
import models.User;
import server.DAO.AuthTokenDAO;
import server.DAO.UserDAO;

import java.io.PushbackReader;

public class LoginCommand implements CommandInterface {
    private ServerFacade server;

    public LoginCommand(ServerFacade server) {
        this.server = server;
    }

    @Override
    public void execute(String[] commandParts) {
        if (commandParts.length != 3 ) {
            System.out.println("Usage: login <USERNAME> <PASSWORD>");
            return;
        }

        String username = commandParts[1];
        String password = commandParts[2];

        server.login(username, password);
    }
}