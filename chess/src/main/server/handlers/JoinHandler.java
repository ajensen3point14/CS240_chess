package server.handlers;

import server.ServerResponse;
import server.requests.JoinRequest;
import server.services.JoinService;

public class JoinHandler extends Handler{
    @Override
    public ServerResponse handleRequest(String input, String authToken) {
        JoinRequest request = gson.fromJson(input, JoinRequest.class);
        request.setAuthToken(authToken);

        JoinService joinService = new JoinService();
        joinService.join(request);

        return new ServerResponse("{}");

    }
}
