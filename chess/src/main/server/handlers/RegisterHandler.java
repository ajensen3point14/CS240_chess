package server.handlers;

import server.ServerResponse;
import server.requests.RegisterRequest;
import server.results.Result;
import server.services.RegisterService;

public class RegisterHandler extends Handler {
    @Override
    public ServerResponse handleRequest(String input, String authToken) {
        RegisterRequest request = gson.fromJson(input, RegisterRequest.class);
        RegisterService registerService = new RegisterService();
        Result result = registerService.register(request);

        return new ServerResponse(gson.toJson(result));
    }
}
