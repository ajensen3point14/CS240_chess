package results;

/**
 * Result of a login request, looking at username and its authToken
 */
public class LoginResult{
    String username;
    String authToken;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
