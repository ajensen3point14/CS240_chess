package server.services;

import server.DAO.AuthTokenDAO;
import server.DAO.UserDAO;
import server.models.AuthToken;
import server.requests.LogoutRequest;
import server.results.LogoutResult;

public class LogoutService {
    public LogoutResult logout(LogoutRequest req) {
        LogoutResult res = new LogoutResult();

        AuthTokenDAO authTokenDAO = AuthTokenDAO.getInstance();
        authTokenDAO.remove(req.getAuthToken());

        return res;
    }
}
