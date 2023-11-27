package requests;

/**
 * This authtoken request works for both the logout and list games handlers, since
 * they require the same thing
 */
public class AuthTokenRequest {
    String authToken;

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
