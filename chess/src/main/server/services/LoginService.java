package server.services;

import server.DAO.AuthTokenDAO;
import server.DAO.UserDAO;
import server.models.AuthToken;
import server.requests.LoginRequest;
import server.results.LoginResult;
import server.results.RegisterResult;

public class LoginService {
    public LoginResult login(LoginRequest req) {
        LoginResult res = new LoginResult();

        UserDAO userDao = UserDAO.getInstance();
        userDao.find(req.getUsername(), req.getPassword());
        AuthTokenDAO authTokenDAO = AuthTokenDAO.getInstance();
        AuthToken myAuthToken = authTokenDAO.createOrFind(req.getUsername());

        res.setUsername(myAuthToken.getUsername());
        res.setAuthToken(myAuthToken.getAuthToken());

        return res;
    }
}
