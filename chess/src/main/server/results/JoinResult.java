package server.results;

import java.util.ArrayList;

public class JoinResult {
    int gameID;
    String whiteUsername;
    String blackUsername;
    ArrayList<String> observers = new ArrayList<>();

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

    public ArrayList<String> getObservers() {
        return observers;
    }

    public void setObservers(ArrayList<String> observers) {
        this.observers = observers;
    }
}
