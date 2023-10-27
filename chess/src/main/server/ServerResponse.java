package server;

public class ServerResponse {
    String body;
    int statusCode;

    public ServerResponse(String body, int statusCode) {
        this.body = body;
        this.statusCode = statusCode;
    }

    public ServerResponse(String body) {
        this.body = body;
        this.statusCode = 0;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
