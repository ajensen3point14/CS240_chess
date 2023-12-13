package clientUI;

import ClientWebSockets.WSClient;
import WSShared.OldGameCommand;
import WSShared.GameCommand;
import chess.*;
import chess.pieces.MyPiece;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import commands.HelpCommand;
import models.Game;
import requests.*;
import results.*;
import ui.EscapeSequences;
import webSocketMessages.userCommands.UserGameCommand;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;

import static ui.EscapeSequences.*;


public class ServerFacade {
    String myURI = "http://localhost:8080";
    WSClient ws;
    String token;
    int currGameID;
    ListResult gamesList;

    private boolean loggedIn = false;
    protected Gson gson = null;


    public ServerFacade(WSClient ws) {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(ChessPiece.class, new ChessPieceDeserializer());
        gson = builder.create();
        this.ws = ws;
    }


    public ServerFacade() {}

    public MyGame getMyGame() { return ws.getMyGame(); }

    public String getToken() {
        return token;
    }

    public int getCurrGameID() {
        return currGameID;
    }

    public ListResult getGamesList() {
        return gamesList;
    }
    public String getWSClientPlayerColor() { return ws.getPlayerColor(); }
    public void setWSClientPlayerColor(String color) { ws.setPlayerColor(color); }

    public void doWebSocketRequest(UserGameCommand cmd) {
        String webRequest = gson.toJson(cmd);
        try {
            ws.send(webRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void register(String username, String password, String email) {
        // create register request
        RegisterRequest req = new RegisterRequest();
        req.setEmail(email);
        req.setUsername(username);
        req.setPassword(password);

        // Serialize request to JSON
        String myReq = gson.toJson(req);
        // doRequest
        String myResult = doRequest(myReq, "POST", "/user");
        // Deserialize JSON
        RegisterResult result = gson.fromJson(myResult, RegisterResult.class);
        // Save authToken
        token = result.getAuthToken();

        // Log user in
        login(username, password);
    }

    public void login(String username, String password) {
        LoginRequest req = new LoginRequest();
        req.setPassword(password);
        req.setUsername(username);

        String myReq = gson.toJson(req);
        String myResult = doRequest(myReq, "POST", "/session");
        LoginResult result = gson.fromJson(myResult, LoginResult.class);
        token = result.getAuthToken();
        loggedIn = true;

        // Display postlogin help UI
        String[] postLogin = {};
        HelpCommand help = new HelpCommand(loggedIn);
        help.execute(postLogin);
    }

    public void logout() {
        AuthTokenRequest req = new AuthTokenRequest();
        req.setAuthToken(token);

        String myReq = gson.toJson(req);
        doRequest(myReq, "DELETE", "/session");
        loggedIn = false;

        String[] postLogin = {};
        HelpCommand help = new HelpCommand(loggedIn);
        help.execute(postLogin);
    }

    public void create(String gameName) {
        CreateRequest req = new CreateRequest();
        req.setGameName(gameName);
        req.setAuthToken(token);

        String myReq = gson.toJson(req);
        String myResult = doRequest(myReq, "POST", "/game");
        CreateResult result = gson.fromJson(myResult, CreateResult.class);
        currGameID = result.getGameID();
    }

    public void list() {
        String myResult = doRequest(null, "GET", "/game");
        gamesList = gson.fromJson(myResult, ListResult.class);
        if (gamesList.getGames().isEmpty()) {
            System.out.println("No games to display");
        } else {
            int listNum = 1;
            System.out.println("Game List:");
            for (ListResultItem game : gamesList.getGames()) {
                StringBuilder s = new StringBuilder();
                s.append("  Game ").append(listNum)
                        .append(": Name: ").append(game.getGameName())
                        .append(", White: ").append(game.getWhiteUsername())
                        .append(", Black: ").append(game.getBlackUsername())
                        .append(", ID: ").append(game.getGameID());
                System.out.println(s);
                listNum++;
            }
            System.out.println("End game list");
        }
    }

    // This method addresses joining as either a player or an observer (in which case color is null)
    public void join(int gameNum, String color) {
        if (!loggedIn) { throw new MyClientException("Must be logged in"); }
        if (gamesList == null) { throw new MyClientException("Must list games first"); }
        if (gameNum > gamesList.getGames().size() || gameNum <= 0) {
            throw new MyClientException("Invalid game number");
        }
        JoinRequest req = new JoinRequest();
        req.setPlayerColor(color);
        req.setAuthToken(token);
        req.setGameID(gamesList.getGames().get(gameNum - 1).getGameID());
        doRequest(gson.toJson(req), "PUT", "/game");

        GameCommand cmd = null;
        if (color == null) {
            cmd = new GameCommand(token, UserGameCommand.CommandType.JOIN_OBSERVER);
        } else {
            cmd = new GameCommand(token, UserGameCommand.CommandType.JOIN_PLAYER);
            cmd.setPlayerColor(color.equalsIgnoreCase("white") ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK);
        }
        cmd.setGameID(gamesList.getGames().get(gameNum - 1).getGameID());

        doWebSocketRequest(cmd);
        currGameID = cmd.getGameID();
    }

    public void leave(int gameID) {
        GameCommand cmd = new GameCommand(token, UserGameCommand.CommandType.LEAVE);
        cmd.setGameID(currGameID);

        doWebSocketRequest(cmd);
        currGameID = -1;

        // Display postlogin help UI
        String[] postLogin = {};
        HelpCommand help = new HelpCommand(loggedIn);
        help.execute(postLogin);
    }

    public void resign() {
        GameCommand cmd = new GameCommand(token, UserGameCommand.CommandType.RESIGN);
        cmd.setGameID(currGameID);

        doWebSocketRequest(cmd);
    }

    public void move(String move) {
        GameCommand gameCmd = new GameCommand(token, UserGameCommand.CommandType.MAKE_MOVE);
        gameCmd.setGameID(currGameID);

        int startCol = move.charAt(0) - 'a' + 1;
        int startRow = move.charAt(1) - '1' + 1;
        int endCol = move.charAt(2) - 'a' + 1;
        int endRow = move.charAt(3) - '1' + 1;

        ChessPiece piece = ws.getMyGame().getBoard().getPiece(new MyPosition(startRow, startCol));
        String promotionPieceStr = null;
        ChessPiece.PieceType promotionPiece = null;

        // Handle piece promotion
        if (piece != null && piece.getPieceType().equals(ChessPiece.PieceType.PAWN)) {
            if (endRow == 8 || endRow == 1) {
                while (true) {
                    System.out.println("Pawn promotion piece (Q, R, B, N): ");
                    promotionPieceStr = ConsoleInputReader.readInput();
                    if ("Q".equals(promotionPieceStr)) {
                        promotionPiece = ChessPiece.PieceType.QUEEN;
                        break;
                    } else if ("R".equals(promotionPieceStr)) {
                        promotionPiece = ChessPiece.PieceType.ROOK;
                        break;
                    } else if ("N".equals(promotionPieceStr)) {
                        promotionPiece = ChessPiece.PieceType.KNIGHT;
                        break;
                    } else if ("B".equals(promotionPieceStr)) {
                        promotionPiece = ChessPiece.PieceType.BISHOP;
                        break;
                    }
                    System.out.println("Invalid promotion piece");
                }
            }
        }

        MyMove myMove = new MyMove(new MyPosition(startRow, startCol), new MyPosition(endRow, endCol), promotionPiece);
        gameCmd.setMove(myMove);

        doWebSocketRequest(gameCmd);
    }

    // Used for unit testing -- clear the DB each time
    public void clear() {
        doRequest(null, "DELETE", "/db");
    }

    // Need a method that will handle sending/receiving the server
    private String doRequest(String request, String verb, String path) {
        try {
            HttpRequest.Builder builder = HttpRequest.newBuilder().uri(new URI(myURI + path));
            if (loggedIn && token != null) {
                builder.header("authorization", token);
            }
            if (Objects.equals(verb, "GET")) {
                builder = builder.GET();
            } else if (Objects.equals(verb, "POST")) {
                builder = builder.POST(HttpRequest.BodyPublishers.ofString(request));
            } else if (Objects.equals(verb, "DELETE")) {
                builder = builder.DELETE();
            } else if (Objects.equals(verb, "PUT")) {
                builder = builder.PUT(HttpRequest.BodyPublishers.ofString(request));
            }
            HttpRequest myRequest = builder.build();
            HttpClient myClient = HttpClient.newHttpClient();
            HttpResponse<String> response = myClient.send(myRequest, HttpResponse.BodyHandlers.ofString());

            System.out.println(response.body());

            if (response.statusCode() != 200) {
                throw new MyClientException("Error getting server response" + response.body());
            }

            return response.body();
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public boolean getLoggedIn() {
        return loggedIn;
    }
}
