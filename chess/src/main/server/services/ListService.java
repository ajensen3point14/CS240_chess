package server.services;

import server.DAO.AuthTokenDAO;
import server.DAO.GameDAO;
import server.requests.AuthTokenRequest;
import server.results.ListResult;
import server.results.ListResultItem;

public class ListService {
    public ListResult list(AuthTokenRequest req) {
        // verify that the user is logged in
        AuthTokenDAO authTokenDAO = AuthTokenDAO.getInstance();
        authTokenDAO.find(req.getAuthToken());

        return new ListResult(GameDAO.getInstance().findAll());
    }
}
