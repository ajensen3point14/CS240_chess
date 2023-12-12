package clientUI;

import ClientWebSockets.WSClient;
import WSShared.OldGameCommand;
import WSShared.GameCommand;
import chess.*;
import chess.pieces.MyPiece;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import commands.HelpCommand;
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

        // Display postlogin help UI
        String[] postLogin = {};
        HelpCommand help = new HelpCommand(loggedIn);
        help.execute(postLogin);
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

        GameCommand cmd = new GameCommand(token, UserGameCommand.CommandType.JOIN_PLAYER);
        cmd.setGameID(gamesList.getGames().get(gameNum - 1).getGameID());
        cmd.setPlayerColor(color.equalsIgnoreCase("white") ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK);

        doWebSocketRequest(cmd);
        currGameID = cmd.getGameID();
    }

    public void leave(int gameID) {
        JoinRequest req = new JoinRequest();
        req.setGameID(gameID);

        String myReq = gson.toJson(req);
        OldGameCommand gameCmd = new OldGameCommand(token);
        gameCmd.setSerializedRequest(myReq);
        gameCmd.setCommandType(UserGameCommand.CommandType.LEAVE);

        doWebSocketRequest(gameCmd);
        currGameID = -1;

        // Display postlogin help UI
        String[] postLogin = {};
        HelpCommand help = new HelpCommand(loggedIn);
        help.execute(postLogin);
    }

    public void resign(int gameID) {
        JoinRequest req = new JoinRequest();
        req.setGameID(gameID);

        String myReq = gson.toJson(req);
        OldGameCommand gameCmd = new OldGameCommand(token);
        gameCmd.setSerializedRequest(myReq);
        gameCmd.setCommandType(UserGameCommand.CommandType.RESIGN);

        doWebSocketRequest(gameCmd);
        currGameID = -1;
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

    public void displayBoard(String orientation) {
        // Top border
        System.out.print(SET_BG_COLOR_LIGHT_GREY);
        if (Objects.equals(orientation, "black")) {
            System.out.print("    h  g  f  e  d  c  b  a    ");
        }
        if (Objects.equals(orientation, "white")) {
            System.out.print("    a  b  c  d  e  f  g  h    ");
        }
        System.out.print(EscapeSequences.RESET_BG_COLOR);
        System.out.println();

        String pawn_color;
        if (Objects.equals(orientation, "white")) {
            pawn_color = SET_TEXT_COLOR_RED;
        } else {
            pawn_color = SET_TEXT_COLOR_BLUE;
        }

        // top pieces
        // Back row
        drawSquare(SET_BG_COLOR_LIGHT_GREY, SET_TEXT_COLOR_BLACK, getRowNum(8, orientation));
        if (Objects.equals(orientation, "black")) {
            drawSquare(SET_BG_COLOR_WHITE, pawn_color, " R ");
            drawSquare(SET_BG_COLOR_BLACK, pawn_color, " N ");
            drawSquare(SET_BG_COLOR_WHITE, pawn_color, " B ");
            drawSquare(SET_BG_COLOR_BLACK, pawn_color, " K ");
            drawSquare(SET_BG_COLOR_WHITE, pawn_color, " Q ");
            drawSquare(SET_BG_COLOR_BLACK, pawn_color, " B ");
            drawSquare(SET_BG_COLOR_WHITE, pawn_color, " N ");
            drawSquare(SET_BG_COLOR_BLACK, pawn_color, " R ");
        } else {
            drawSquare(SET_BG_COLOR_WHITE, pawn_color, " R ");
            drawSquare(SET_BG_COLOR_BLACK, pawn_color, " N ");
            drawSquare(SET_BG_COLOR_WHITE, pawn_color, " B ");
            drawSquare(SET_BG_COLOR_BLACK, pawn_color, " Q ");
            drawSquare(SET_BG_COLOR_WHITE, pawn_color, " K ");
            drawSquare(SET_BG_COLOR_BLACK, pawn_color, " B ");
            drawSquare(SET_BG_COLOR_WHITE, pawn_color, " N ");
            drawSquare(SET_BG_COLOR_BLACK, pawn_color, " R ");
        }
        drawSquare(SET_BG_COLOR_LIGHT_GREY, SET_TEXT_COLOR_BLACK, getRowNum(8, orientation));
        System.out.println();

        // Row 7
        drawSquare(SET_BG_COLOR_LIGHT_GREY, SET_TEXT_COLOR_BLACK, getRowNum(7, orientation));
        drawSquare(SET_BG_COLOR_BLACK, pawn_color, " P ");
        drawSquare(SET_BG_COLOR_WHITE, pawn_color, " P ");
        drawSquare(SET_BG_COLOR_BLACK, pawn_color, " P ");
        drawSquare(SET_BG_COLOR_WHITE, pawn_color, " P ");
        drawSquare(SET_BG_COLOR_BLACK, pawn_color, " P ");
        drawSquare(SET_BG_COLOR_WHITE, pawn_color, " P ");
        drawSquare(SET_BG_COLOR_BLACK, pawn_color, " P ");
        drawSquare(SET_BG_COLOR_WHITE, pawn_color, " P ");
        drawSquare(SET_BG_COLOR_LIGHT_GREY, SET_TEXT_COLOR_BLACK, getRowNum(7, orientation));
        System.out.println();

        // Row 6
        drawSquare(SET_BG_COLOR_LIGHT_GREY, SET_TEXT_COLOR_BLACK, getRowNum(6, orientation));
        drawSquare(SET_BG_COLOR_WHITE, SET_TEXT_COLOR_BLUE, "   ");
        drawSquare(SET_BG_COLOR_BLACK, SET_TEXT_COLOR_BLUE, "   ");
        drawSquare(SET_BG_COLOR_WHITE, SET_TEXT_COLOR_BLUE, "   ");
        drawSquare(SET_BG_COLOR_BLACK, SET_TEXT_COLOR_BLUE, "   ");
        drawSquare(SET_BG_COLOR_WHITE, SET_TEXT_COLOR_BLUE, "   ");
        drawSquare(SET_BG_COLOR_BLACK, SET_TEXT_COLOR_BLUE, "   ");
        drawSquare(SET_BG_COLOR_WHITE, SET_TEXT_COLOR_BLUE, "   ");
        drawSquare(SET_BG_COLOR_BLACK, SET_TEXT_COLOR_BLUE, "   ");
        drawSquare(SET_BG_COLOR_LIGHT_GREY, SET_TEXT_COLOR_BLACK, getRowNum(6, orientation));
        System.out.println();

        // Row 5
        drawSquare(SET_BG_COLOR_LIGHT_GREY, SET_TEXT_COLOR_BLACK, getRowNum(5, orientation));
        drawSquare(SET_BG_COLOR_BLACK, SET_TEXT_COLOR_BLUE, "   ");
        drawSquare(SET_BG_COLOR_WHITE, SET_TEXT_COLOR_BLUE, "   ");
        drawSquare(SET_BG_COLOR_BLACK, SET_TEXT_COLOR_BLUE, "   ");
        drawSquare(SET_BG_COLOR_WHITE, SET_TEXT_COLOR_BLUE, "   ");
        drawSquare(SET_BG_COLOR_BLACK, SET_TEXT_COLOR_BLUE, "   ");
        drawSquare(SET_BG_COLOR_WHITE, SET_TEXT_COLOR_BLUE, "   ");
        drawSquare(SET_BG_COLOR_BLACK, SET_TEXT_COLOR_BLUE, "   ");
        drawSquare(SET_BG_COLOR_WHITE, SET_TEXT_COLOR_BLUE, "   ");
        drawSquare(SET_BG_COLOR_LIGHT_GREY, SET_TEXT_COLOR_BLACK, getRowNum(5, orientation));
        System.out.println();

        // Row 4
        drawSquare(SET_BG_COLOR_LIGHT_GREY, SET_TEXT_COLOR_BLACK, getRowNum(4, orientation));
        drawSquare(SET_BG_COLOR_WHITE, SET_TEXT_COLOR_BLUE, "   ");
        drawSquare(SET_BG_COLOR_BLACK, SET_TEXT_COLOR_BLUE, "   ");
        drawSquare(SET_BG_COLOR_WHITE, SET_TEXT_COLOR_BLUE, "   ");
        drawSquare(SET_BG_COLOR_BLACK, SET_TEXT_COLOR_BLUE, "   ");
        drawSquare(SET_BG_COLOR_WHITE, SET_TEXT_COLOR_BLUE, "   ");
        drawSquare(SET_BG_COLOR_BLACK, SET_TEXT_COLOR_BLUE, "   ");
        drawSquare(SET_BG_COLOR_WHITE, SET_TEXT_COLOR_BLUE, "   ");
        drawSquare(SET_BG_COLOR_BLACK, SET_TEXT_COLOR_BLUE, "   ");
        drawSquare(SET_BG_COLOR_LIGHT_GREY, SET_TEXT_COLOR_BLACK, getRowNum(4, orientation));
        System.out.println();

        // Row 3
        drawSquare(SET_BG_COLOR_LIGHT_GREY, SET_TEXT_COLOR_BLACK, getRowNum(3, orientation));
        drawSquare(SET_BG_COLOR_BLACK, SET_TEXT_COLOR_BLUE, "   ");
        drawSquare(SET_BG_COLOR_WHITE, SET_TEXT_COLOR_BLUE, "   ");
        drawSquare(SET_BG_COLOR_BLACK, SET_TEXT_COLOR_BLUE, "   ");
        drawSquare(SET_BG_COLOR_WHITE, SET_TEXT_COLOR_BLUE, "   ");
        drawSquare(SET_BG_COLOR_BLACK, SET_TEXT_COLOR_BLUE, "   ");
        drawSquare(SET_BG_COLOR_WHITE, SET_TEXT_COLOR_BLUE, "   ");
        drawSquare(SET_BG_COLOR_BLACK, SET_TEXT_COLOR_BLUE, "   ");
        drawSquare(SET_BG_COLOR_WHITE, SET_TEXT_COLOR_BLUE, "   ");
        drawSquare(SET_BG_COLOR_LIGHT_GREY, SET_TEXT_COLOR_BLACK, getRowNum(3, orientation));
        System.out.println();

        // Bottom pawn row
        drawSquare(SET_BG_COLOR_LIGHT_GREY, SET_TEXT_COLOR_BLACK, getRowNum(2, orientation));
        if (orientation == "white") {
            pawn_color = SET_TEXT_COLOR_BLUE;
        } else {
            pawn_color = SET_TEXT_COLOR_RED;
        }
        drawSquare(SET_BG_COLOR_WHITE, pawn_color, " P ");
        drawSquare(SET_BG_COLOR_BLACK, pawn_color, " P ");
        drawSquare(SET_BG_COLOR_WHITE, pawn_color, " P ");
        drawSquare(SET_BG_COLOR_BLACK, pawn_color, " P ");
        drawSquare(SET_BG_COLOR_WHITE, pawn_color, " P ");
        drawSquare(SET_BG_COLOR_BLACK, pawn_color, " P ");
        drawSquare(SET_BG_COLOR_WHITE, pawn_color, " P ");
        drawSquare(SET_BG_COLOR_BLACK, pawn_color, " P ");
        drawSquare(SET_BG_COLOR_LIGHT_GREY, SET_TEXT_COLOR_BLACK, getRowNum(2, orientation));
        System.out.println();

        // Bottom row
        drawSquare(SET_BG_COLOR_LIGHT_GREY, SET_TEXT_COLOR_BLACK, getRowNum(1, orientation));
        if (orientation == "white") {
            drawSquare(SET_BG_COLOR_BLACK, pawn_color, " R ");
            drawSquare(SET_BG_COLOR_WHITE, pawn_color, " N ");
            drawSquare(SET_BG_COLOR_BLACK, pawn_color, " B ");
            drawSquare(SET_BG_COLOR_WHITE, pawn_color, " Q ");
            drawSquare(SET_BG_COLOR_BLACK, pawn_color, " K ");
            drawSquare(SET_BG_COLOR_WHITE, pawn_color, " B ");
            drawSquare(SET_BG_COLOR_BLACK, pawn_color, " N ");
            drawSquare(SET_BG_COLOR_WHITE, pawn_color, " R ");
        } else {
            drawSquare(SET_BG_COLOR_BLACK, pawn_color, " R ");
            drawSquare(SET_BG_COLOR_WHITE, pawn_color, " N ");
            drawSquare(SET_BG_COLOR_BLACK, pawn_color, " B ");
            drawSquare(SET_BG_COLOR_WHITE, pawn_color, " K ");
            drawSquare(SET_BG_COLOR_BLACK, pawn_color, " Q ");
            drawSquare(SET_BG_COLOR_WHITE, pawn_color, " B ");
            drawSquare(SET_BG_COLOR_BLACK, pawn_color, " N ");
            drawSquare(SET_BG_COLOR_WHITE, pawn_color, " R ");
        }
        drawSquare(SET_BG_COLOR_LIGHT_GREY, SET_TEXT_COLOR_BLACK, getRowNum(1, orientation));
        System.out.println();

        // Bottom border
        System.out.print(SET_BG_COLOR_LIGHT_GREY);
        if (Objects.equals(orientation, "black")) {
            System.out.print("    h  g  f  e  d  c  b  a    ");
        }
        if (Objects.equals(orientation, "white")) {
            System.out.print("    a  b  c  d  e  f  g  h    ");
        }
        System.out.print(EscapeSequences.RESET_BG_COLOR);
        System.out.println();
    }

    private String getRowNum(int val, String orientation) {
        if (orientation == "white") {
            return String.format(" %d ", val);
        } else {
            return String.format(" %d ", 9 - val);
        }
    }

    private void drawSquare(String bg, String fg, String text) {
        System.out.print(bg + fg + text);
        System.out.print(EscapeSequences.RESET_BG_COLOR);
        System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE);
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
