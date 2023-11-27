package commands;

import clientUI.*;

public class CommandDispatcher {
    private final boolean loggedIn;
    private final String username;
    private final ServerFacade server;

    public CommandDispatcher(ServerFacade serverFacade) {
        this.loggedIn = false;
        this.username = null;
        this.server = serverFacade;
    }

    public boolean dispatch(String userInput) {
        String[] commandParts = userInput.split("\\s+");
        String command = commandParts[0].toLowerCase();
        CommandInterface newCommand = null;

        switch (command) {
            case "help":
                newCommand = new HelpCommand(server.getLoggedIn());
                break;
            case "login":
                newCommand = new LoginCommand(server);
                break;
            case "register":
                newCommand = new RegisterCommand(server);
                break;
            case "logout":
                newCommand = new LogoutCommand(server);
                break;
            case "create":
                newCommand = new CreateCommand(server);
                break;
            case "list":
                newCommand = new ListCommand(server);
                break;
            case "join":
                newCommand = new JoinCommand(server);
                break;
            case "observe":
                newCommand = new JoinCommand(server);
                break;
            case "quit":
                // exit the program (don't repeat the client loop)
                return false;
            default:
                System.out.println("Unknown command. Type 'help' for a list of commands.");
        }
        if (newCommand != null) {
            try {
                newCommand.execute(commandParts);
            } catch (MyClientException e) {
                System.out.println(e.toString());
            }
        }
        return true;
    }

    public void setLoggedIn(boolean loggedIn) {
    }
}
