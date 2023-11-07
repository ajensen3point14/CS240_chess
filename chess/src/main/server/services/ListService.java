package server.services;

import server.DAO.AuthTokenDAO;
import server.DAO.GameDAO;
import server.MyServerException;
import server.models.AuthToken;
import server.requests.AuthTokenRequest;
import server.results.ListResult;

/**
 * List all games in a database
 */
public class ListService {
    /**
     * List all the games using the list result
     * @param req the requested user. We will use their authToken to identify
     * @return the list of all database games
     */
    public ListResult list(AuthTokenRequest req) {
        // verify that the user is logged in
        AuthTokenDAO authTokenDAO = AuthTokenDAO.getInstance();
        AuthToken token = authTokenDAO.find(req.getAuthToken());
        if (token == null) {
            throw new MyServerException("Unauthorized", 401);
        }

        return new ListResult(GameDAO.getInstance().findAll());
    }
}
