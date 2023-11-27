package commands;

import clientUI.ServerFacade;

public class LogoutCommand implements CommandInterface{
    private final ServerFacade server;

    public LogoutCommand(ServerFacade server) {
        this.server = server;
    }

    @Override
    public void execute(String[] commandParts) {
        server.logout();
    }
}
