package commands;

import clientUI.ServerFacade;

import java.util.Objects;

import static java.lang.Integer.parseInt;

public class JoinCommand implements CommandInterface{
    private ServerFacade server;

    public JoinCommand(ServerFacade server) {
        this.server = server;
    }

    @Override
    public void execute(String[] commandParts) {
        if (Objects.equals(commandParts[0], "join") && commandParts.length != 3 ) {
            System.out.println("Usage: join <ID> [ WHITE | BLACK ]");
            return;
        } else if (Objects.equals(commandParts[0], "observe") && commandParts.length != 2) {
            System.out.println("Usage: observe <ID>");
            return;
        }

        String color = null;
        int id = parseInt(commandParts[1]);

        // If join was called, a color must be specified
        if (Objects.equals(commandParts[0], "join")) {
            color = commandParts[2];
        }

        server.join(id, color);
    }
}
