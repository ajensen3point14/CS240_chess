package server.handlers;

import com.google.gson.Gson;
import server.ServerResponse;

/**
 * The base handler class that provides a base for the rest of the handlers to use
 * Specifically, the protected Gson variable grants Gson access to the other handlers.
 */
public class Handler {
    protected Gson gson = new Gson();

    /**
     * Basic call to handle a request that it passes on to the required handler
     * @param JSONInput string input in JSON format
     * @param authToken string authToken for the user. This may be null in many cases
     * @return this value is more important in the specific handler classes
     */
    public ServerResponse handleRequest(String JSONInput, String authToken) {
        return null;
    }
}
