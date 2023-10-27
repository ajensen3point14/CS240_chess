package server.handlers;

import server.ServerResponse;
import server.requests.AuthTokenRequest;
import server.results.ListResult;
import server.services.ListService;

public class ListHandler extends Handler{
    @Override
    public ServerResponse handleRequest(String input, String authToken) {
        AuthTokenRequest request = new AuthTokenRequest();
        request.setAuthToken(authToken);
        ListService listService = new ListService();
        ListResult result = listService.list(request);

        return new ServerResponse(gson.toJson(result));
    }
}
