package server.services;

import server.DAO.AuthTokenDAO;
import server.DAO.GameDAO;
import server.DAO.UserDAO;

/**
 * Clear all info by calling the respective database clear() methods
 */
public class ClearService {
    /**
     * Clear all databases
     */
    public void clear() {
        UserDAO.getInstance().clear();
        AuthTokenDAO.getInstance().clear();
        GameDAO.getInstance().clear();
    }
}
