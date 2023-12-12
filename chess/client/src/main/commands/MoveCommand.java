package commands;

import clientUI.ServerFacade;

public class MoveCommand implements CommandInterface {
    private ServerFacade server;

    public MoveCommand(ServerFacade server) { this.server = server; }

    @Override
    public void execute(String[] commandParts) {
        if (commandParts[0].length() != 4 || commandParts.length != 1) {
            System.out.println("Usage: e4e5");
            return;
        }

        server.move(commandParts[0]);
    }
}
