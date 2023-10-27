package server.requests;

/**
 * Request for creating a game
 */
public class CreateRequest {
    String gameName;
    String authToken;

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getGameName() {
        return gameName;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
