package server;

import dataAccess.DataAccessException;
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

        Spark.post("/user", this::user);
        Spark.post("/session", this::session);
        Spark.delete("/session", this::logout);
        Spark.get("/game", this::listGames);
        Spark.post("/game", this::create);
        Spark.put("/game", this::join);
        Spark.delete("/db", this::clear);
        Spark.get("/hello", this::test);
    }

    // This is the single location for all server requests to be run
    private String doRequest(Request req, Response res, Handler handler, String authToken) {
        try {
            ServerResponse info = handler.handleRequest(req.body(), authToken);
            int errorCode = info.getStatusCode();
            if (errorCode != 0) {
                res.status(errorCode);
            }
            return info.getBody();
        } catch(MyServerException e) {
            res.status(e.getHttpErrorCode());
            return String.format("{ \"message\": \"Error: %s\" }", e.getMessage());
        } catch(DataAccessException e) {
            res.status(500);
            return String.format("{ \"message\": \"Error: %s\" }", e.getMessage());
        } catch(Throwable e) {
            res.status(500);
            e.printStackTrace();
            return String.format("{ \"message\": \"Error: %s\" }", e);
        }
    }

    // Handler methods for HTTP endpoints defined above

    private Object user(Request req, Response res) {
        return doRequest(req, res, new RegisterHandler(), null);
    }

    private Object session(Request req, Response res) {
        return doRequest(req, res, new LoginHandler(), null);
    }

    private Object logout(Request req, Response res) {
        String token = req.headers("authorization");
        res.type("text/plain");
        return doRequest(req, res, new LogoutHandler(), token);
    }

    private Object listGames(Request req, Response res) {
        String token = req.headers("authorization");
        return doRequest(req, res, new ListHandler(), token);
    }

    private Object create(Request req, Response res) {
        String token = req.headers("authorization");
        return doRequest(req, res, new CreateHandler(), token);
    }

    private Object join(Request req, Response res) {
        String token = req.headers("authorization");
        return doRequest(req, res, new JoinHandler(), token);
    }

    private Object clear(Request req, Response res) {
        return doRequest(req, res, new ClearHandler(), null);
    }

    private Object test(Request req, Response res) {
        return "";
    }
}