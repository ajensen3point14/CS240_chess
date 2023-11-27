package models;

/**
 * Creates an AuthToken associated with a given username
 */
public class AuthToken {
    String authToken;
    String username;

    /**
     * Creates a new authToken for the specific user. Both are in string format.
     * @param authToken
     * @param username
     */
    public AuthToken(String authToken, String username) {
        this.authToken = authToken;
        this.username = username;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getUsername() {
        return username;
    }
}
