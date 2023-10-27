package server.handlers;

import com.google.gson.Gson;
import server.ServerResponse;

public class Handler {
    protected Gson gson = new Gson();
    public ServerResponse handleRequest(String JSONInput, String authToken) {
        return null;
    }
}
