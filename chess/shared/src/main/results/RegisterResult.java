package results;

/**
 * Result of a register request, providing a username and an authToken
 */
public class RegisterResult{
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
