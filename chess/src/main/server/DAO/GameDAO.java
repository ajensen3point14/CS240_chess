package server.DAO;

import server.MyServerException;
import server.models.Game;

import java.util.Collection;
import java.util.HashMap;

/**
 * Interactions between the database and the list of games.
 * These are stored for now as a HashMap, but this will change in phase 4.
 * The maximum number of supported games is defined here.
 */
public class GameDAO implements DAO{
    // Define the max number of simultaneous games the server supports
    private static final int MAXGAMES = 1000;
    HashMap<Integer, Game> games = new HashMap<Integer, Game>();

    // Singleton pattern for games
    private static GameDAO single_instance = null;
    public static synchronized GameDAO getInstance(){
        if (single_instance == null)
            single_instance = new GameDAO();

        return single_instance;
    }

    /**
     * Add a game to the database. Throw a max capacity error if full.
     * This will set a gameID, and set the given name for the game.
     * @param gameName string name for this game. This must be unique
     * @throws MyServerException bad request if there is no provided game name
     * @throws MyServerException max capacity 503 if server is full
     * @return game that is associated with this name
     */
    public Game addGame(String gameName) {
        if (gameName == null || gameName.isEmpty()) {
            throw new MyServerException("bad request", 400);
        }
        for (int ii = 1; ii < MAXGAMES; ii++) {
            if (!games.containsKey(ii)) {
                Game newGame = new Game();
                newGame.setGameID(ii);
                newGame.setGameName(gameName);
                games.put(ii, newGame);
                return newGame;
            }
        }
        throw new MyServerException("server at max game capacity", 503);
    }

    /**
     * Find a specific game in the database. Throw a bad request error if the game is not found
     * @param id identifier that will be used to find the game
     * @throws MyServerException bad request if the game is not in the database
     * @return the found game
     */
    public Game find(int id) {
        if (!games.containsKey(id)) {
            throw new MyServerException("bad request", 400);
        }
        return games.get(id);
    }

    /**
     * @return a list of all games in the database
     */
    public Collection<Game> findAll() {
        return games.values();
    }

    /**
     * This method is not used in the current implementation of the chess server,
     * but the idea is clear: remove a specific game from the database.
     * @param id identifier of game to be removed
     * @throws MyServerException bad request if the keyID is not in the database
     */
    public void remove(int id) {
        if (!games.containsKey(id)) {
            throw new MyServerException("bad request", 400);
        }
        games.remove(id);
    }

    /**
     * clear the games database
     */
    @Override
    public void clear() {
        games.clear();
    }
}
