package server.services;

import server.DAO.AuthTokenDAO;
import server.DAO.GameDAO;
import server.MyServerException;
import server.models.AuthToken;
import server.models.Game;
import server.requests.CreateRequest;
import server.results.CreateResult;

import java.util.Objects;

/**
 * Create a new game in the database
 */
public class CreateService {
    /**
     *
     * @param req contains the authtoken necessary to create the new game
     * @return the result of the new game
     */
    public CreateResult create(CreateRequest req) {
        if (req.getGameName() == null || Objects.equals(req.getGameName(), "")) {
            throw new MyServerException("Bad request", 400);
        }
        CreateResult res = new CreateResult();

        // verify that the user is logged in
        AuthTokenDAO authTokenDAO = AuthTokenDAO.getInstance();
        AuthToken token = authTokenDAO.find(req.getAuthToken());
        if (token == null) {
            throw new MyServerException("Unauthorized", 401);
        }

        GameDAO gameDAO = GameDAO.getInstance();
        Game newGame = gameDAO.addGame(new Game(0, req.getGameName()));

        res.setGameID(newGame.getGameID());

        return res;
    }
}
