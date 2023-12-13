package commands;

import clientUI.ConsoleInputReader;
import clientUI.ServerFacade;

import java.util.Objects;

import static java.lang.Integer.parseInt;

public class ResignCommand implements CommandInterface{

    private ServerFacade server;

    public ResignCommand(ServerFacade server) {
        this.server = server;
    }

    public void execute(String[] commandParts) {
        if (Objects.equals(commandParts[0], "resign") && commandParts.length != 1 ) {
            System.out.println("Usage: resign");
            return;
        }
        int id = server.getCurrGameID();


        if (confirmResignation()) {
            server.resign();
        } else {
            System.out.println("Resignation canceled.");
        }
    }

    private boolean confirmResignation() {
        System.out.print("Are you sure you want to resign? (yes/no): ");

        return ConsoleInputReader.readInput().equals("yes");
    }
}
