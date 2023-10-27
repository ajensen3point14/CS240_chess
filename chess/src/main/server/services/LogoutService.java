package server.services;

import server.DAO.AuthTokenDAO;
import server.requests.AuthTokenRequest;

/**
 * Log a user out
 */
public class LogoutService {
    /**
     * Log a user out by removing their authToken from the database
     * @param req the requested logout authtoken
     */
    public void logout(AuthTokenRequest req) {
        AuthTokenDAO authTokenDAO = AuthTokenDAO.getInstance();
        authTokenDAO.remove(req.getAuthToken());
    }
}
