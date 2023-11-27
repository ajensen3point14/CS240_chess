package commands;

import clientUI.ServerFacade;

public class RegisterCommand implements CommandInterface{
    private ServerFacade server;
    public RegisterCommand(ServerFacade server) {
        this.server = server;
    }
    @Override
    public void execute(String[] commandParts) {
        if (commandParts.length != 4 ) {
            System.out.println("Usage: register <USERNAME> <PASSWORD> <EMAIL>");
            return;
        }

        String username = commandParts[1];
        String password = commandParts[2];
        String email = commandParts[3];

        server.register(username, password, email);
    }
}
