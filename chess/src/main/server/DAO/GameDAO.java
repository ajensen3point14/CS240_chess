package server.DAO;

import server.MyServerException;
import server.models.Game;

import java.util.Collection;
import java.util.HashMap;

public class GameDAO extends DAO{
    HashMap<Integer, Game> games = new HashMap<Integer, Game>();

    private static GameDAO single_instance = null;
    public static synchronized GameDAO getInstance(){
        if (single_instance == null)
            single_instance = new GameDAO();

        return single_instance;
    }
    public Game addGame(String gameName) {
        if (gameName.isEmpty()) {
            throw new MyServerException("bad request", 400);
        }
        int maxGames = 1000;
        for (int ii = 0; ii < maxGames; ii++) {
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
    public Game find(int id) {
        if (!games.containsKey(id)) {
            throw new MyServerException("bad request", 400);
        }
        return games.get(id);
    }
    public Collection<Game> findAll() {
        return games.values();
    }
    public void remove(int id) {
        if (!games.containsKey(id)) {
            throw new MyServerException("bad request", 400);
        }
        games.remove(id);
    }
    public void claimSpot() {

    }
    public void updateGame() {

    }
    @Override
    public void clear() {
        games.clear();
    }
}
