package server.services;

import server.DAO.AuthTokenDAO;
import server.DAO.GameDAO;
import server.MyServerException;
import models.AuthToken;
import models.Game;
import requests.JoinRequest;

/**
 * Join a game
 */
public class JoinService {
    /**
     * Join a game, setting the players to the provided color, and making them an observer if none
     * is provided.
     * @throws MyServerException already taken if a player is assigned that side
     * @throws MyServerException bad request if an illegal color is provided
     * @param req the join requested by the user
     */
    public void join(JoinRequest req) {
        // verify that the user is logged in
        AuthTokenDAO authTokenDAO = AuthTokenDAO.getInstance();
        AuthToken user = authTokenDAO.find(req.getAuthToken());
        if (user == null) {
            throw new MyServerException("Unauthorized", 401);
        }
        // Set player sides based on provided color

        Game myGame = GameDAO.getInstance().find(req.getGameID());
        if (myGame == null) {
            throw new MyServerException("Bad request", 400);
        }
        if ("WHITE".equals(req.getPlayerColor())) {
            if (myGame.getWhiteUsername() != null && !myGame.getWhiteUsername().equals(user.getUsername())) {
                throw new MyServerException("already taken", 403);
            }
            myGame.setWhiteUsername(user.getUsername());

        } else if ("BLACK".equals(req.getPlayerColor())) {
            if (myGame.getBlackUsername() != null && !myGame.getBlackUsername().equals(user.getUsername())) {
                throw new MyServerException("already taken", 403);
            }
            myGame.setBlackUsername(user.getUsername());

        } else if (req.getPlayerColor() == null || req.getPlayerColor().isEmpty()) {
            // if no color is provided, the user is a game observer
            if (!myGame.getObservers().contains(user.getUsername())) {
                myGame.getObservers().add(user.getUsername());
            }

        }else {
            throw new MyServerException("bad request", 400);
        }
        GameDAO.getInstance().update(myGame);
    }
}
