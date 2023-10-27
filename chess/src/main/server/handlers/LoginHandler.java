package server.handlers;

import server.ServerResponse;
import server.requests.LoginRequest;
import server.results.LoginResult;
import server.services.LoginService;

import java.util.HashMap;

public class LoginHandler extends Handler{
    @Override
    public ServerResponse handleRequest(String input, String authToken) {
        LoginRequest request = gson.fromJson(input, LoginRequest.class);
        LoginService loginService = new LoginService();
        LoginResult result = loginService.login(request);

        return new ServerResponse(gson.toJson(result));
    }
}
