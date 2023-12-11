package commands;

import clientUI.ServerFacade;

import java.util.Objects;

import static java.lang.Integer.parseInt;

public class LeaveCommand implements CommandInterface{
    private ServerFacade server;

    public LeaveCommand(ServerFacade server) {
        this.server = server;
    }

    @Override
    public void execute(String[] commandParts) {
        if (Objects.equals(commandParts[0], "leave") && commandParts.length != 2 ) {
            System.out.println("Usage: leave [gameID]");
            return;
        }
        int id = parseInt(commandParts[1]);

        server.leave(id);
    }
}
