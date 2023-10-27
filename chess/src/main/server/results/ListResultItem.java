package server.results;

import server.models.Game;

public class ListResultItem{
    int gameID;
    String whiteUsername;
    String blackUsername;
    String gameName;

    public ListResultItem(Game g) {
        this.gameID = g.getGameID();
        this.whiteUsername = g.getWhiteUsername();
        this.blackUsername = g.getBlackUsername();
        this.gameName = g.getGameName();
    }

    public int getGameID() {
        return gameID;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    public String getWhiteUsername() {
        return whiteUsername;
    }

    public void setWhiteUsername(String whiteUsername) {
        this.whiteUsername = whiteUsername;
    }

    public String getBlackUsername() {
        return blackUsername;
    }

    public void setBlackUsername(String blackUsername) {
        this.blackUsername = blackUsername;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }
}
