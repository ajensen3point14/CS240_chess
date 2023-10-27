package server.services;

import server.DAO.AuthTokenDAO;
import server.DAO.UserDAO;

public class ClearService {
    public void clear() {
        UserDAO userDAO = UserDAO.getInstance();
        userDAO.clear();
        AuthTokenDAO authTokenDAO = AuthTokenDAO.getInstance();
        authTokenDAO.clear();
    }
}
