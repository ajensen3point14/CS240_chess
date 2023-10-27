package server.results;

import server.models.Game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ListResult extends Result{
    ArrayList<ListResultItem> games = new ArrayList<>();

    public ListResult(Collection<Game> gameList) {
        for (Game game : gameList) {
            games.add(new ListResultItem(game));
        }
    }

    public ArrayList<ListResultItem> getGames() {
        return games;
    }

    public void setGames(ArrayList<ListResultItem> games) {
        this.games = games;
    }
}
