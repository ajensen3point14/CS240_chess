package server.services;

import server.DAO.AuthTokenDAO;
import server.DAO.UserDAO;
import server.MyServerException;
import server.models.AuthToken;
import server.models.User;
import server.requests.RegisterRequest;
import server.results.RegisterResult;

import java.util.Objects;

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
        if (req.getUsername() == null || req.getPassword() == null ||
                Objects.equals(req.getUsername(), "") || Objects.equals(req.getPassword(), "")) {
            throw new MyServerException("Bad request", 400);
        }

        UserDAO userDAO = UserDAO.getInstance();
        User user = userDAO.find(req.getUsername(), req.getPassword());
        if (user == null) {
            userDAO.insert(new User(req.getUsername(), req.getPassword(), req.getEmail()));
            AuthTokenDAO authTokenDAO = AuthTokenDAO.getInstance();
            AuthToken myAuthToken = authTokenDAO.create(req.getUsername());

            res.setUsername(myAuthToken.getUsername());
            res.setAuthToken(myAuthToken.getAuthToken());
        } else {
            throw new MyServerException("Unauthorized", 403);
        }

        return res;
    }
}
