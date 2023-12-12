package WSShared;

import models.Game;
import webSocketMessages.serverMessages.ServerMessage;

public class GameMessage extends ServerMessage {
    private Game game;
    private String message;
    private String errorMessage;

    public GameMessage(ServerMessageType type) {
        super(type);
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }
}
