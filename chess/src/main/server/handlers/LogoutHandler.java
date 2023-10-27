package server.handlers;

import server.ServerResponse;
import server.requests.LogoutRequest;
import server.results.LogoutResult;
import server.services.LogoutService;

public class LogoutHandler extends Handler{
    @Override
    public ServerResponse handleRequest(String input, String authToken) {
        LogoutRequest request = new LogoutRequest();
        request.setAuthToken(authToken);
        LogoutService logoutService = new LogoutService();
        LogoutResult result = logoutService.logout(request);

        return new ServerResponse(gson.toJson(result));
    }
}
