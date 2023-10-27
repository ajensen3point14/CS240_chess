package server.requests;

// this works for any handler that just wants an authToken: logout and listgames
// TODO: is this a good idea?
public class AuthTokenRequest {
    String authToken;

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
