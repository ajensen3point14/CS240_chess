package server.handlers;

import server.ServerResponse;
import requests.RegisterRequest;
import results.RegisterResult;
import server.services.RegisterService;

/**
 * Handle new user registration
 */
public class RegisterHandler extends Handler {
    /**
     * Create a new user and generate an authToken for them
     * @param input string input in JSON format
     * @param authToken string authToken for the user. This will be null, but filled in by the register request
     * @return the username and their new authToken
     */
    @Override
    public ServerResponse handleRequest(String input, String authToken) {
        RegisterRequest request = gson.fromJson(input, RegisterRequest.class);
        RegisterService registerService = new RegisterService();
        RegisterResult result = registerService.register(request);

        return new ServerResponse(gson.toJson(result));
    }
}
