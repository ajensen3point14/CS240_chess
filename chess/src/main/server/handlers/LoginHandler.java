package server.handlers;

import server.ServerResponse;
import server.requests.LoginRequest;
import server.results.LoginResult;
import server.services.LoginService;

/**
 * Handles login requests
 */
public class LoginHandler extends Handler{
    /**
     * Use the LoginService to authenticate a user
     * @param input string input in JSON format
     * @param authToken string authToken for the user. This will be null here.
     * @return
     */
    @Override
    public ServerResponse handleRequest(String input, String authToken) {
        LoginRequest request = gson.fromJson(input, LoginRequest.class);
        LoginService loginService = new LoginService();
        LoginResult result = loginService.login(request);

        return new ServerResponse(gson.toJson(result));
    }
}
