package server.DAO;

import server.MyServerException;
import server.models.AuthToken;

import java.util.HashMap;
import java.util.UUID;

public class AuthTokenDAO extends DAO{
    // maps authToken to a username
    HashMap<String, AuthToken> authTokens = new HashMap<String, AuthToken>();
    private static AuthTokenDAO single_instance = null;
    public static synchronized AuthTokenDAO getInstance(){
        if (single_instance == null)
            single_instance = new AuthTokenDAO();

        return single_instance;
    }
    public AuthToken createOrFind(String name) {
        if (name.isEmpty()) {
            throw new MyServerException("bad request", 400);
        }
        if (!authTokens.containsKey(name)) {
            // If the user doesn't already have an authToken, generate one and insert it into the map
            // We insert both ways so that we have instant lookup of either the user or the token
            String token = UUID.randomUUID().toString();
            AuthToken newToken = new AuthToken(token, name);
            authTokens.put(token, newToken);
            authTokens.put(name, newToken);
        }
        return authTokens.get(name);
    }
}
