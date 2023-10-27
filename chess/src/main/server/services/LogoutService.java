package server.services;

import server.DAO.AuthTokenDAO;
import server.requests.AuthTokenRequest;
import server.results.LogoutResult;

public class LogoutService {
    public LogoutResult logout(AuthTokenRequest req) {
        LogoutResult res = new LogoutResult();

        AuthTokenDAO authTokenDAO = AuthTokenDAO.getInstance();
        authTokenDAO.remove(req.getAuthToken());

        return res;
    }
}
