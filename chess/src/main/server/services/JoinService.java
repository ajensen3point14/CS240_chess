package server.services;

import dataAccess.DataAccessException;
import server.DAO.AuthTokenDAO;
import server.DAO.GameDAO;
import server.MyServerException;
import models.AuthToken;
import models.Game;
import requests.JoinRequest;

import java.util.Objects;

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
    public Game join(JoinRequest req) {
        // verify that the user is logged in
        AuthTokenDAO authTokenDAO = AuthTokenDAO.getInstance();
        AuthToken user = authTokenDAO.find(req.getAuthToken());
        if (user == null) {
            throw new MyServerException("Unauthorized", 401);
        }
        // Set player sides based on provided color

        Game game = GameDAO.getInstance().find(req.getGameID());
        if (game == null) {
            throw new MyServerException("Bad request", 400);
        }
        if ("WHITE".equals(req.getPlayerColor())) {
            if (game.getWhiteUsername() != null && !game.getWhiteUsername().equals(user.getUsername())) {
                throw new MyServerException("already taken", 403);
            }
            game.setWhiteUsername(user.getUsername());

        } else if ("BLACK".equals(req.getPlayerColor())) {
            if (game.getBlackUsername() != null && !game.getBlackUsername().equals(user.getUsername())) {
                throw new MyServerException("already taken", 403);
            }
            game.setBlackUsername(user.getUsername());

        } else if (req.getPlayerColor() == null || req.getPlayerColor().isEmpty()) {
            // if no color is provided, the user is a game observer
            if (!game.getObservers().contains(user.getUsername())) {
                game.getObservers().add(user.getUsername());
            }

        }else {
            throw new MyServerException("bad request", 400);
        }
        GameDAO.getInstance().update(game);
        return game;
    }

    public void unjoin(JoinRequest req) {
        // verify that the user is logged in
        AuthTokenDAO authTokenDAO = AuthTokenDAO.getInstance();
        AuthToken user = authTokenDAO.find(req.getAuthToken());
        if (user == null) {
            throw new MyServerException("Unauthorized", 401);
        }

        Game myGame = GameDAO.getInstance().find(req.getGameID());
        if (myGame == null) {
            throw new MyServerException("Bad request", 400);
        }

        // Remove user from the game
        if (Objects.equals(myGame.getWhiteUsername(), user.getUsername())) {
            myGame.setWhiteUsername(null);
        } else if (Objects.equals(myGame.getBlackUsername(), user.getUsername())) {
            myGame.setBlackUsername(null);
        } else {
            if (!myGame.getObservers().contains(user.getUsername())) {
                throw new DataAccessException("Game not joined");
            }
            myGame.getObservers().remove(user.getUsername());
        }

        // Update the DAO
        GameDAO.getInstance().update(myGame);
    }
}
