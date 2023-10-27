package server.services;

import server.DAO.AuthTokenDAO;
import server.DAO.GameDAO;
import server.models.Game;
import server.requests.CreateRequest;
import server.results.CreateResult;

public class CreateService {
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
