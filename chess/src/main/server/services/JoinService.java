package server.services;

import server.DAO.AuthTokenDAO;
import server.DAO.GameDAO;
import server.MyServerException;
import server.models.AuthToken;
import server.models.Game;
import server.requests.JoinRequest;

import java.util.Objects;

public class JoinService {
    public void join(JoinRequest req) {
        // verify that the user is logged in
        AuthTokenDAO authTokenDAO = AuthTokenDAO.getInstance();
        AuthToken user = authTokenDAO.find(req.getAuthToken());

        Game myGame = GameDAO.getInstance().find(req.getGameID());
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
            if (!myGame.getObservers().contains(user.getUsername())) {
                myGame.getObservers().add(user.getUsername());
            }
        }else {
            throw new MyServerException("bad request", 400);
        }
    }
}
