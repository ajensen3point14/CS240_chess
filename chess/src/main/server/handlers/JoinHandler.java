package server.handlers;

import server.ServerResponse;
import server.requests.JoinRequest;
import server.services.JoinService;

/**
 * Handles join requests
 */
public class JoinHandler extends Handler{
    /**
     * Handle a join request, returning empty
     * @param input string input in JSON format
     * @param authToken string authToken for the user. This may be null in many cases
     * @return empty, per the project specs
     */
    @Override
    public ServerResponse handleRequest(String input, String authToken) {
        JoinRequest request = gson.fromJson(input, JoinRequest.class);
        request.setAuthToken(authToken);

        JoinService joinService = new JoinService();
        joinService.join(request);

        return new ServerResponse("{}");

    }
}
