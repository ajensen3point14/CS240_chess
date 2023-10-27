package server.handlers;

import server.ServerResponse;
import server.requests.AuthTokenRequest;
import server.results.ListResult;
import server.services.ListService;

/**
 * List all of the games in the database
 */
public class ListHandler extends Handler{
    /**
     * Use the AuthTokenRequest to set an authtoken and return a list of for that authToken
     * @param input string input in JSON format
     * @param authToken string authToken for the user. This will not be null here
     * @return a list of games in the database
     */
    @Override
    public ServerResponse handleRequest(String input, String authToken) {
        AuthTokenRequest request = new AuthTokenRequest();
        request.setAuthToken(authToken);
        ListService listService = new ListService();
        ListResult result = listService.list(request);

        return new ServerResponse(gson.toJson(result));
    }
}
