package server.handlers;

import server.ServerResponse;
import server.requests.AuthTokenRequest;
import server.services.LogoutService;

/**
 * Handle logout requests
 */
public class LogoutHandler extends Handler{
    /**
     * Use the AuthTokenRequest to set an authToken, then log the user out of the session
     * @param input string input in JSON format
     * @param authToken string authToken for the user. This will not be null here
     * @return empty, per the specs.
     */
    @Override
    public ServerResponse handleRequest(String input, String authToken) {
        AuthTokenRequest request = new AuthTokenRequest();
        request.setAuthToken(authToken);
        LogoutService logoutService = new LogoutService();
        logoutService.logout(request);

        return new ServerResponse("{}");
    }
}
