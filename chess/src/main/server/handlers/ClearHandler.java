package server.handlers;

import server.ServerResponse;
import server.services.ClearService;

public class ClearHandler extends Handler{
    @Override
    public ServerResponse handleRequest(String input, String authToken) {
        ClearService clearService = new ClearService();
        clearService.clear();

        return new ServerResponse("{}");
    }
}
