package server.services;

import server.DAO.AuthTokenDAO;
import server.DAO.UserDAO;
import server.MyServerException;
import models.AuthToken;
import models.User;
import requests.LoginRequest;
import results.LoginResult;

/**
 * Log a user in
 */
public class LoginService {
    /**
     * A new authtoken is given to a user when they log in, per the project test cases
     * @param req the login request
     * @return the login result
     */
    public LoginResult login(LoginRequest req) {
        LoginResult res = new LoginResult();

        UserDAO userDAO = UserDAO.getInstance();
        User user = userDAO.find(req.getUsername(), req.getPassword());
        if (user == null) {
            throw new MyServerException("Unauthorized", 401);
        }
        AuthTokenDAO authTokenDAO = AuthTokenDAO.getInstance();
        AuthToken myAuthToken = authTokenDAO.find(req.getUsername());

        // if the user is already logged in, remove the authtoken to provide a new one (per the test cases)
        if (myAuthToken != null) {
            authTokenDAO.remove(myAuthToken.getAuthToken());
        }
        myAuthToken = authTokenDAO.create(req.getUsername());

        res.setUsername(myAuthToken.getUsername());
        res.setAuthToken(myAuthToken.getAuthToken());

        return res;
    }
}
