package server;

import models.Game;

/**
 * messages and status of the server after it responds to a request
 */
public class ServerResponse {
    Game game;
    String body;
    int statusCode;

    /**
     * Sets status code and populates the message body appropriately - status is 0 by default
     * @param body message body
     */
    public ServerResponse(String body) {
        this.body = body;
        this.statusCode = 0;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getBody() {
        return body;
    }

    public int getStatusCode() {
        return statusCode;
    }

}
