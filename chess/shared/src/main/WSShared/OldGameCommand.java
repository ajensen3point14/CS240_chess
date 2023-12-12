package WSShared;

import webSocketMessages.userCommands.UserGameCommand;

import java.io.Serializable;

public class OldGameCommand extends UserGameCommand implements Serializable {
    private String gameBoard;
    private String serializedRequest;


    public OldGameCommand(String authToken) {
        super(authToken);
    }

    public String getSerializedRequest() {
        return serializedRequest;
    }

    public void setSerializedRequest(String serializedRequest) {
        this.serializedRequest = serializedRequest;
    }

    public void setCommandType(CommandType commandType) {
        this.commandType = commandType;
    }

    public String getGameBoard() {
        return gameBoard;
    }

    public void setGameBoard(String gameBoard) {
        this.gameBoard = gameBoard;
    }
}
