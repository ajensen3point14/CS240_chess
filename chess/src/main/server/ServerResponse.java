package server;

/**
 * messages and status of the server after it responds to a request
 */
public class ServerResponse {
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

    public String getBody() {
        return body;
    }

    public int getStatusCode() {
        return statusCode;
    }

}
