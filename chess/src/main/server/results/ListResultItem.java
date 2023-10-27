package server.results;

import server.models.Game;

/**
 * Helper that generates an individual game to add to the list of games
 */
public class ListResultItem{
    int gameID;
    String whiteUsername;
    String blackUsername;
    String gameName;

    /**
     * Create a list result from a provided game object
     * @param game the provided game
     */
    public ListResultItem(Game game) {
        this.gameID = game.getGameID();
        this.whiteUsername = game.getWhiteUsername();
        this.blackUsername = game.getBlackUsername();
        this.gameName = game.getGameName();
    }
}
