package commands;

import clientUI.ServerFacade;

public class CreateCommand implements CommandInterface{
    private final ServerFacade server;

    public CreateCommand(ServerFacade server) {
        this.server = server;
    }

    @Override
    public void execute(String[] commandParts) {
        if (commandParts.length != 2 ) {
            System.out.println("Usage: create <GAME_NAME>");
            return;
        }

        String gameName = commandParts[1];

        server.create(gameName);
    }
}
