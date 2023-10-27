package server.services;

import server.DAO.AuthTokenDAO;
import server.DAO.UserDAO;
import server.models.AuthToken;
import server.requests.RegisterRequest;
import server.results.RegisterResult;

public class RegisterService {
    public RegisterResult register(RegisterRequest req) {
        RegisterResult res = new RegisterResult();

        UserDAO userDAO = UserDAO.getInstance();
        userDAO.insert(req.getUsername(), req.getPassword(), req.getEmail());
        AuthTokenDAO authTokenDAO = AuthTokenDAO.getInstance();
        AuthToken myAuthToken = authTokenDAO.createOrFind(req.getUsername());

        res.setUsername(myAuthToken.getUsername());
        res.setAuthToken(myAuthToken.getAuthToken());

        return res;
    }
}
