package server;

import server.handlers.*;
import spark.*;

/** Responsible for running the server and listening for requests */
public class Server {
    /**
     * Takes a port number and starts a server on that port
     * @param args Command line argument that specifies where the server should run
     */
    public static void main(String[] args) {
        String portNum = "8080";
        if (args.length != 0) { portNum = args[0]; }
        new Server().run(portNum);
    }

    /**
     * Actually runs the server and implements the web API endpoints
     * @param portNum Port on which the server will run
     */
    private void run(String portNum) {
        Spark.port(Integer.parseInt(portNum));

        // Register director for hosting static files
        Spark.externalStaticFileLocation("C:\\Users\\aaron\\Documents\\GitHub\\chess\\chess\\web");

        // TODO: register handlers for each endpoint
        Spark.post("/user", this::user);
        Spark.post("/session", this::session);

        return;
    }
    private String doRequest(Request req, Response res, Handler handler) {
        try {
            ServerResponse info = handler.handleRequest(req.body());
            int errorCode = info.getStatusCode();
            if (errorCode != 0) {
                res.status(errorCode);
            }
            return info.getBody();
        } catch(MyServerException e) {
            res.status(e.getHttpErrorCode());
            return String.format("{ \"message\": \"Error: %s\" }", e.getMessage());
        } catch(Throwable e) {
            res.status(500);
            e.printStackTrace();
            return String.format("{ \"message\": \"Error: %s\" }", e);
        }
    }
    private Object user(Request req, Response res) {
        return doRequest(req, res, new RegisterHandler());
    }
    private Object session(Request req, Response res) {
        return doRequest(req, res, new LoginHandler());
    }
}
