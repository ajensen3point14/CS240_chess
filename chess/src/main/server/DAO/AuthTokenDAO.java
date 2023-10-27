package server.DAO;

import server.MyServerException;
import server.models.AuthToken;

import java.util.HashMap;
import java.util.UUID;

/**
 * Interactions between AuthTokens and the database are stored here.
 * AuthTokens are stored in a hashmap in two ways: once as the token to the username, and
 * the other as a username to the token, providing order one lookup in either direction.
 */
public class AuthTokenDAO implements DAO{
    // maps authToken to a username
    HashMap<String, AuthToken> authTokens = new HashMap<String, AuthToken>();

    // AuthToken singleton
    private static AuthTokenDAO single_instance = null;
    public static synchronized AuthTokenDAO getInstance(){
        if (single_instance == null)
            single_instance = new AuthTokenDAO();

        return single_instance;
    }

    /**
     * Creates a new authToken for the given username
     * @param name string username that will be associated with the token
     * @throws MyServerException bad request if name is empty
     * @throws  MyServerException already taken if the user is already in the database
     * @return the token associated with the given username
     */
    public AuthToken create(String name) {
        if (name.isEmpty()) {
            throw new MyServerException("bad request", 400);
        }
        if (authTokens.containsKey(name)) {
            throw new MyServerException("already taken", 403);
        }
        // If the user doesn't already have an authToken, generate one and insert it into the map
        // We insert both ways so that we have instant lookup of either the user or the token
        String token = UUID.randomUUID().toString();
        AuthToken newToken = new AuthToken(token, name);
        authTokens.put(token, newToken);
        authTokens.put(name, newToken);

        return authTokens.get(name);
    }

    /**
     * Find the authToken for the associated username.
     * @throws MyServerException if token is not found, throw "unauthorized"
     * @throws MyServerException if the name provided is empty, throw "bad request"
     * @param name String username used to identify the token.
     * @return the authToken
     */
    public AuthToken find(String name) {
        if (name.isEmpty()) {
            throw new MyServerException("bad request", 400);
        }
        if (!authTokens.containsKey(name)) {
            throw new MyServerException("unauthorized", 401);
        }
        return authTokens.get(name);
    }

    /**
     * Remove the token from the map. The remove call occurs twice because we inserted the token
     * twice, once from token to username, and once from username to token
     * @param token string authToken that will be deleted
     * @throws MyServerException unauthorized if the token is empty or already exists in the database
     */
    public void remove(String token) {
        if (token.isEmpty() || !authTokens.containsKey(token)) {
            throw new MyServerException("unauthorized", 401);
        }
        AuthToken myToken = find(token);
        if (myToken != null) {
            authTokens.remove(myToken.getAuthToken());
            authTokens.remove(myToken.getUsername());
        }
    }

    /**
     * clear the authTokens hashMap
     */
    @Override
    public void clear() {
        authTokens.clear();
    }
}
