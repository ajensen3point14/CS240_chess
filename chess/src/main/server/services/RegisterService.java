package server.services;

import server.DAO.AuthTokenDAO;
import server.DAO.UserDAO;
import server.models.AuthToken;
import server.requests.RegisterRequest;
import server.results.RegisterResult;

/**
 * Service registering a new user
 */
public class RegisterService {
    /**
     * Register a new user per the requested info
     * @param req the requested info to register a user
     * @return the result of the setting of the username and authToken
     */
    public RegisterResult register(RegisterRequest req) {
        RegisterResult res = new RegisterResult();

        UserDAO userDAO = UserDAO.getInstance();
        userDAO.insert(req.getUsername(), req.getPassword(), req.getEmail());
        AuthTokenDAO authTokenDAO = AuthTokenDAO.getInstance();
        AuthToken myAuthToken = authTokenDAO.create(req.getUsername());

        res.setUsername(myAuthToken.getUsername());
        res.setAuthToken(myAuthToken.getAuthToken());

        return res;
    }
}
