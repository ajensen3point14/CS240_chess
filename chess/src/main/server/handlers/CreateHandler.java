package server.handlers;

import server.ServerResponse;
import server.requests.AuthTokenRequest;
import server.requests.CreateRequest;
import server.results.CreateResult;
import server.services.CreateService;

public class CreateHandler extends Handler{
    public ServerResponse handleRequest(String input, String authToken) {
        CreateRequest request = gson.fromJson(input, CreateRequest.class);
        request.setAuthToken(authToken);
        CreateService createService = new CreateService();
        CreateResult result = createService.create(request);

        return new ServerResponse(gson.toJson(result));
    }
}
