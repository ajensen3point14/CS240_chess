package server.handlers;

import server.ServerResponse;
import server.requests.CreateRequest;
import server.results.CreateResult;
import server.services.CreateService;

/**
 * Handler that creates a new game
 */
public class CreateHandler extends Handler{
    /**
     * Handle the create request
     * @param input the game name
     * @param authToken the authToken header associated with the user creating the game
     * @return the game ID
     */
    public ServerResponse handleRequest(String input, String authToken) {
        CreateRequest request = gson.fromJson(input, CreateRequest.class);
        request.setAuthToken(authToken);
        CreateService createService = new CreateService();
        CreateResult result = createService.create(request);

        return new ServerResponse(gson.toJson(result));
    }
}
