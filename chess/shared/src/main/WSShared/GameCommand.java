package WSShared;

import chess.ChessGame;
import chess.ChessMove;
import webSocketMessages.userCommands.UserGameCommand;

public class GameCommand extends UserGameCommand {
    private Integer gameID;
    private ChessMove move;
    private ChessGame.TeamColor playerColor;

    public GameCommand(String authToken, CommandType cmd) {
        super(authToken);
        commandType = cmd;
    }

    public Integer getGameID() {
        return gameID;
    }

    public void setGameID(Integer gameID) {
        this.gameID = gameID;
    }

    public ChessMove getMove() {
        return move;
    }

    public void setMove(ChessMove move) {
        this.move = move;
    }

    public ChessGame.TeamColor getPlayerColor() {
        return playerColor;
    }

    public void setPlayerColor(ChessGame.TeamColor playerColor) {
        this.playerColor = playerColor;
    }
}
