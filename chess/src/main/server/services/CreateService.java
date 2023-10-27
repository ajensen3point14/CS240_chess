package server.services;

import server.DAO.AuthTokenDAO;
import server.DAO.GameDAO;
import server.models.Game;
import server.requests.CreateRequest;
import server.results.CreateResult;

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
        CreateResult res = new CreateResult();

        // verify that the user is logged in
        AuthTokenDAO authTokenDAO = AuthTokenDAO.getInstance();
        authTokenDAO.find(req.getAuthToken());

        GameDAO gameDAO = GameDAO.getInstance();
        Game newGame = gameDAO.addGame(req.getGameName());

        res.setGameID(newGame.getGameID());

        return res;
    }
}
