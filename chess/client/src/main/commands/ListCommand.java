package commands;

import clientUI.ServerFacade;
import server.Server;

public class ListCommand implements CommandInterface{
    private ServerFacade server;

    public ListCommand(ServerFacade server) {
        this.server = server;
    }

    @Override
    public void execute(String[] commandParts) {
        server.list();
    }
}
