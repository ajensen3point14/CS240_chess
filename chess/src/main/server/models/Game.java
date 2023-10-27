package server.models;

import chess.MyGame;

import java.util.ArrayList;

public class Game {
    int gameID;
    String whiteUsername;
    String blackUsername;
    String gameName;
    ArrayList<String> observers = new ArrayList<String>();
    MyGame game;

    public ArrayList<String> getObservers() {
        return observers;
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

    public MyGame getGame() {
        return game;
    }

    public void setGame(MyGame game) {
        this.game = game;
    }
}
