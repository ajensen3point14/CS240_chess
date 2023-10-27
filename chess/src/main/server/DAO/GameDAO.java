package server.DAO;

import server.MyServerException;
import server.models.AuthToken;
import server.models.Game;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public class GameDAO extends DAO{
    HashMap<Integer, Game> games = new HashMap<Integer, Game>();

    private static GameDAO single_instance = null;
    public static synchronized GameDAO getInstance(){
        if (single_instance == null)
            single_instance = new GameDAO();

        return single_instance;
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
